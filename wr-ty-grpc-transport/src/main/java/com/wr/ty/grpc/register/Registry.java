package com.wr.ty.grpc.register;

import com.google.protobuf.GeneratedMessageV3;
import com.wr.ty.grpc.util.ChangeNotifications;
import com.xh.demo.grpc.WrTy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author weirui (xiao hei)
 *
 * @date 2019/4/7 16:56
 */
//todo change date
public class Registry {
    private static final Logger logger = LoggerFactory.getLogger(Registry.class);
    protected final ConcurrentHashMap<String, WrTy.InstanceInfo> instanceDataSource;
    private final Scheduler.Worker worker;
    FluxSink<WrTy.ChangeNotification> registryChangePublisher;
    Flux<WrTy.ChangeNotification> fluxRegistryChange;

    public Registry(Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        this.instanceDataSource = new ConcurrentHashMap<>();
        this.worker = scheduler.createWorker();
        EmitterProcessor<WrTy.ChangeNotification> emitterProcessor = EmitterProcessor.create();
        fluxRegistryChange = emitterProcessor.share();
        registryChangePublisher = emitterProcessor.sink();
    }

    /**
     * @param changeNotification
     */
    public void register(WrTy.ChangeNotification changeNotification) {
        this.worker.schedule(() -> processNotification(changeNotification));
    }

    public Flux<WrTy.ChangeNotification> subscriber() {
        return Flux.empty();
    }

    /**
     * note: ChangeNotification is sequential ,but is possible repetition
     *
     * @param interest
     * @return
     */
    public Flux<WrTy.ChangeNotification> interest(WrTy.Interest interest) {
        Collection<WrTy.ChangeNotification> values = instanceDataSource.values().stream().map(value -> ChangeNotifications.newAddNotification(value)).collect(Collectors.toList());
        Flux<WrTy.ChangeNotification> initData = Flux.fromIterable(values);
        Flux<WrTy.ChangeNotification> changeNotificationFlux = Flux.mergeSequential(initData, fluxRegistryChange);
        return changeNotificationFlux;
    }

    /**
     * Assume single threaded access
     */
    private void processNotification(WrTy.ChangeNotification notification) {
        try {
            WrTy.ChangeNotification.NotificationOneofCase oneofCase = notification.getNotificationOneofCase();
            switch (oneofCase) {
                case ADD:
                    WrTy.InstanceInfo instanceInfo = notification.getAdd().getInstanceInfo();
                    instanceDataSource.put(instanceInfo.getId(), instanceInfo);
                    logger.debug("add instance {}",instanceInfo.toString());
                case MODIFY:
                    // todo wait complete
                    break;
                case DELETE:
                    String id = notification.getDelete().getInstanceId();
                    WrTy.InstanceInfo removedInstance = instanceDataSource.remove(id);
                    logger.debug("delete instance {}",id);

                    break;
                default:
                    logger.error("Unexpected notification type {}", oneofCase);
            }
            // propagate notification to other subscriber or replication
            registryChangePublisher.next(notification);
        } catch (Exception e) {
            logger.error("Error processing the notification in the registry: {}", notification, e);
        }
    }


}
