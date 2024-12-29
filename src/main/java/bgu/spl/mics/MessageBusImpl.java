package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.*;

import java.util.concurrent.*;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues;
	private final ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> subscriptions;
	private final ConcurrentHashMap<Event<?>, Future<?>> eventFutures;
	private final ConcurrentHashMap<MicroService, ConcurrentHashMap<Class<? extends Message>, Callback<?>>> callbacks;

	private static class MessageBusImplHolder {
		private static final MessageBusImpl INSTANCE = new MessageBusImpl();
	}

	public MessageBusImpl() {
		subscriptions = new ConcurrentHashMap<>();
		microServiceQueues = new ConcurrentHashMap<>();
		eventFutures = new ConcurrentHashMap<>();
		this.callbacks = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.INSTANCE;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscriptions.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscriptions.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutures.remove(e);
		if (future != null) {
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Queue<MicroService> microServices = subscriptions.get(b.getClass());
		if (microServices != null) {
			for (MicroService m : microServices) {
				BlockingQueue<Message> queue = microServiceQueues.get(m); // in order to check if null
				if (queue != null) {
					queue.add(b);
				}
			}
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Queue<MicroService> microServices = subscriptions.get(e.getClass());
		if (microServices == null || microServices.isEmpty()) {
			return null; // No microservices subscribed to this event
		}
		MicroService m;
		synchronized (microServices) {
			m = microServices.poll(); // Round-robin
			if (m != null) {
				microServices.add(m);
			}
		}

		if (m != null) {
			BlockingQueue<Message> queue = microServiceQueues.get(m);
			if (queue != null) {
				queue.add(e);
				Future<T> future = new Future<>();
				eventFutures.put(e, future);
				return future;
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		BlockingQueue<Message> queue = microServiceQueues.remove(m);
		if (queue != null) {
			queue.clear();
		}
		subscriptions.values().forEach(q -> q.remove(m));
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = microServiceQueues.get(m);
		if (queue == null) {
			throw new IllegalStateException("Microservice not registered: " + m);
		}
		return queue.take();
	}

	/**
	 * Registers a callback for a specific message type and microservice.
	 */
	public <M extends Message> void registerCallback(Class<M> type, Callback<M> callback, MicroService microService) {
		callbacks.computeIfAbsent(microService, k -> new ConcurrentHashMap<>()).put(type, callback);
	}

	/**
	 * Retrieves the callback for a specific message type and microservice.
	 */
	public <M extends Message> Callback<M> getCallback(Class<M> type, MicroService microService) {
		ConcurrentHashMap<Class<? extends Message>, Callback<?>> serviceCallbacks = callbacks.get(microService);
		if (serviceCallbacks != null) {
			@SuppressWarnings("unchecked")
			Callback<M> callback = (Callback<M>) serviceCallbacks.get(type);
			return callback;
		}
		return null;
	}
}