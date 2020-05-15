//package com.wr.ty.grpc.service;
//
//import com.wr.ty.grpc.TransportConfig;
//import com.wr.ty.grpc.core.DefaultPipelineNameGenerator;
//import com.wr.ty.DefaultRegistry;
//import com.wr.ty.api.Registry;
//import com.wr.ty.grpc.register.TestRegistry;
//import com.xh.demo.grpc.RegistrationServiceGrpc;
//import com.xh.demo.grpc.SubscribeServiceGrpc;
//import com.xh.demo.grpc.WrTy;
//import io.grpc.ManagedChannel;
//import io.grpc.Server;
//import io.grpc.inprocess.InProcessChannelBuilder;
//import io.grpc.inprocess.InProcessServerBuilder;
//import io.grpc.stub.StreamObserver;
//import io.grpc.util.MutableHandlerRegistry;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import reactor.test.publisher.TestPublisher;
//import reactor.test.scheduler.VirtualTimeScheduler;
//
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author xiaohei
// * @date 2020/3/1 14:03
// */
//public abstract class ServerTestBase<IN, OUT> {
//
//    final MutableHandlerRegistry serviceRegistry = new MutableHandlerRegistry();
//    Server server;
//    ManagedChannel channel;
//    TestPublisher<OUT> responseFlux = TestPublisher.create();
//    StreamObserver<IN> requestPublish;
//    VirtualTimeScheduler scheduler = VirtualTimeScheduler.create();
//    TransportConfig config;
//    DefaultPipelineNameGenerator nameGenerator;
//    TestRegistry registry;
//
//    @Before
//    public void parentSetup() throws IOException {
//        String serverName = InProcessServerBuilder.generateName();
//        server = InProcessServerBuilder.forName(serverName)
//                .fallbackHandlerRegistry(serviceRegistry).directExecutor().build().start();
//        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
//        registry = buildRegistry();
//        config = new TransportConfig() {
//        };
//        nameGenerator = new DefaultPipelineNameGenerator();
//    }
//
//    @After
//    public void parentTearDown() throws Exception {
//        if (server != null && !server.isShutdown()) {
//            server.shutdown();
//        }
//        if (channel != null && !channel.isShutdown()) {
//            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//        }
//    }
//
//    protected TestRegistry buildRegistry(){
//        return new TestRegistry();
//    }
//}
