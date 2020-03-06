package com.wr.ty.grpc.register;

import com.wr.ty.grpc.RegistrySubscriber;
import com.wr.ty.grpc.handler.server.ServerSubscribeHandler;
import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;
import reactor.core.scheduler.Scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public class DefaultRegistry implements Registry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRegistry.class);
    protected final ConcurrentHashMap<String, WrTy.InstanceInfo> instanceDataSource;
    private final Scheduler.Worker worker;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    final TopicProcessor<WrTy.ChangeNotification> registryTopic;
    private Set<Subscription> subscriptions;

    public DefaultRegistry(Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        this.instanceDataSource = new ConcurrentHashMap<>();
        this.worker = scheduler.createWorker();
        registryTopic = TopicProcessor.create();
        subscriptions = new HashSet<>();
    }

    /**
     * @param changeNotification
     */
    @Override
    public void register(WrTy.ChangeNotification changeNotification) {
        this.worker.schedule(() -> processNotification(changeNotification));
    }

    @Override
    public void subscribe(RegistrySubscriber subscriber, WrTy.Interest interest) {
        try {
            readLock.lock();
            Collection<WrTy.ChangeNotification> values = instanceDataSource.values().stream().map(value -> ChangeNotifications.newAddNotification(value)).collect(Collectors.toList());
            Flux<WrTy.ChangeNotification> changeNotificationFlux = Flux.fromIterable(values).concatWith(registryTopic);
            changeNotificationFlux.subscribe(subscriber);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int observers() {
        return subscriptions.size();
    }

    @Override
    public void shutDown() {
        worker.dispose();
    }

    /**
     * Assume single threaded access
     */
    private void processNotification(WrTy.ChangeNotification notification) {
        try {
            writeLock.lock();
            WrTy.ChangeNotification.NotificationOneofCase oneofCase = notification.getNotificationOneofCase();
            switch (oneofCase) {
                case ADD:
                    WrTy.InstanceInfo instanceInfo = notification.getAdd().getInstanceInfo();
                    instanceDataSource.put(instanceInfo.getId(), instanceInfo);
                    logger.debug("add instance {}", instanceInfo.toString());
                case MODIFY:
                    // todo wait complete
                    break;
                case DELETE:
                    String id = notification.getDelete().getInstanceId();
                    WrTy.InstanceInfo removedInstance = instanceDataSource.remove(id);
                    logger.debug("delete instance {}", id);

                    break;
                default:
                    logger.error("Unexpected notification type {}", oneofCase);
            }
            // propagate notification to other subscriber or replication
            registryTopic.onNext(notification);
        } catch (Exception e) {
            logger.error("Error processing the notification in the registry: {}", notification, e);
        } finally {
            writeLock.unlock();
        }
    }


}
