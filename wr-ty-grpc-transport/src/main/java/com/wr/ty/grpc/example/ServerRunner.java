///*
// * Copyright 2017 The gRPC Authors
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.wr.ty.grpc.example;
//
//import com.wr.ty.grpc.TransportConfig;
//import com.wr.ty.grpc.core.DefaultPipelineNameGenerator;
//import com.wr.ty.DefaultRegistry;
//import com.wr.ty.api.Registry;
//import com.wr.ty.grpc.service.RegistrationServerImpl;
//import com.wr.ty.grpc.service.ReplicationServerImpl;
//import com.wr.ty.grpc.service.SubscribeServerImpl;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import reactor.core.scheduler.Scheduler;
//import reactor.core.scheduler.Schedulers;
//
//import java.io.IOException;
//
//public class ServerRunner {
//    private static final Logger logger =
//            LoggerFactory.getLogger(ServerRunner.class.getName());
//
//    private static Scheduler scheduler = Schedulers.newElastic("serverChannelHandler");
//
//    public static void main(String[] args) throws InterruptedException, IOException {
//        // Service class implementation
//        Registry registry = new DefaultRegistry(scheduler);
//        TransportConfig config = new TransportConfig() {
//        };
//        DefaultPipelineNameGenerator nameGenerator = new DefaultPipelineNameGenerator();
//        RegistrationServerImpl registerService = new RegistrationServerImpl(registry,config,nameGenerator, scheduler);
//        SubscribeServerImpl subscribeServer = new SubscribeServerImpl(registry,config,nameGenerator, scheduler);
//        ReplicationServerImpl replicationServer = new ReplicationServerImpl(registry,config,nameGenerator, scheduler);
//        final Server server = ServerBuilder
//                .forPort(50051)
//                .addService(registerService)
//                .addService(subscribeServer)
//                .addService(replicationServer)
//                .build()
//                .start();
//
//        logger.info("Listening on " + server.getPort());
//
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                logger.info("Shutting down");
//                server.shutdown();
//            }
//        });
//        server.awaitTermination();
//    }
//
//}
