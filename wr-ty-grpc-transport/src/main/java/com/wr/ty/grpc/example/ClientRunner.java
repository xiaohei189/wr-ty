package com.wr.ty.grpc.example;

import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.client.RegistrationClient;
import com.wr.ty.grpc.client.SubscribeClient;
import com.wr.ty.grpc.core.DefaultServerResolver;
import com.xh.demo.grpc.WrTy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * @author xiaohei
 * @date 2020/2/13 13:30
 */
public class ClientRunner {

    public static void main(String[] args) throws InterruptedException {
        TransportConfig transportConfig = new TransportConfig() {
        };
        Scheduler scheduler = Schedulers.newElastic("client----");
        DefaultServerResolver serverResolver = new DefaultServerResolver();
        RegistrationClient registrationClient1 = new RegistrationClient(scheduler, serverResolver, transportConfig, null);

        registrationClient1.register(Flux.create(value -> {
            value.next(mockInstance("serviceId-1"));
        })).subscribe(value -> {
            System.out.printf(value);
        });
        RegistrationClient registrationClient2 = new RegistrationClient(scheduler, serverResolver, transportConfig, null);
        registrationClient2.register(Flux.create(value -> {
            value.next(mockInstance("serviceId-2"));
        })).subscribe(value -> {
            System.out.printf(value);
        });
        WrTy.Interest.AllInterest allInterest = WrTy.Interest.AllInterest.getDefaultInstance();
        WrTy.Interest interest = WrTy.Interest.newBuilder().setAll(allInterest).build();
        SubscribeClient subscribeClient1 = new SubscribeClient(scheduler, serverResolver, transportConfig, null);
        subscribeClient1.subscribe(interest).subscribe(value -> {
            System.out.println(value);
        });
//
//        SubscribeClient subscribeClient2 = new SubscribeClient(scheduler, serverResolver, transportConfig, null);
//        subscribeClient2.subscribe(interest).subscribe(value -> {
//            System.out.println(value);
//        });


    }

    public static WrTy.InstanceInfo mockInstance(String serviceId) {
        WrTy.InstanceInfo defaultInstance = WrTy.InstanceInfo.newBuilder().setId(serviceId).build();
        return defaultInstance;
    }


}
