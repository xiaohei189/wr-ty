package com.wr.ty.grpc.example;

import com.wr.ty.grpc.client.InterestClient;
import com.wr.ty.grpc.client.RegistrationClient;
import com.wr.ty.grpc.core.DefaultServerResolver;
import com.wr.ty.grpc.TransportConfig;
import com.wr.ty.grpc.register.Registry;
import com.xh.demo.grpc.WrTy;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * @author xiaohei
 * @date 2020/2/13 13:30
 */
public class ClientRunner {

    public static void main(String[] args) throws InterruptedException {
        TransportConfig transportConfig = new TransportConfig() {
        };
        RegistrationClient client = new RegistrationClient(Schedulers.newElastic("client----"), new DefaultServerResolver(), transportConfig, null);
        EmitterProcessor<WrTy.InstanceInfo> emitterProcessor = EmitterProcessor.create();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.register(Flux.just(mockInstance("serviceId-1"))).subscribe(value -> {
            System.out.printf(value);
        });
        RegistrationClient client2 = new RegistrationClient(Schedulers.newElastic("client----"), new DefaultServerResolver(), transportConfig, null);
        client2.register(Flux.just(mockInstance("serviceId-2"))).subscribe(value -> {
            System.out.printf(value);
        });
        Registry registry = new Registry(Schedulers.newElastic("client----"));

        InterestClient interestClient = new InterestClient(Schedulers.newElastic("client----"), new DefaultServerResolver(), transportConfig, registry, null);
        WrTy.Interest.AllInterest allInterest = WrTy.Interest.AllInterest.getDefaultInstance();
        WrTy.Interest interest = WrTy.Interest.newBuilder().setAll(allInterest).build();
        interestClient.subscribe(interest).subscribe(value -> {
            System.out.println(value);
        });
        Thread.sleep(1000 * 10);
        client2.shutDown();
        countDownLatch.await();
    }

    public static WrTy.InstanceInfo mockInstance(String serviceId) {
        WrTy.InstanceInfo defaultInstance = WrTy.InstanceInfo.newBuilder().setId(serviceId).build();
        return defaultInstance;
    }


}
