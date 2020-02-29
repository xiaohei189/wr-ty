package com.wr.ty.grpc.service;

import com.wr.ty.grpc.core.channel.ChannelPipeline;
import com.wr.ty.grpc.core.channel.ChannelPipelineFactory;
import com.wr.ty.grpc.handler.server.RegistrationProcessorHandler;
import com.wr.ty.grpc.handler.server.ServerHandshakeHandler;
import com.wr.ty.grpc.handler.server.ServerHeartbeatHandler;
import com.wr.ty.grpc.handler.server.ServerLoggingChannelHandler;
import com.wr.ty.grpc.register.Registry;
import com.wr.ty.grpc.util.SourceIdGenerator;
import com.xh.demo.grpc.RegistrationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xiaohei
 * @date 2020/2/12 22:54
 */
public class RegistrationServerImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServerImpl.class);

    private final ChannelPipelineFactory registrationPipelineFactory;
    private final Registry register;

    public RegistrationServerImpl(Registry registry, Scheduler scheduler) {
        Objects.requireNonNull(scheduler);
        Objects.requireNonNull(registry);
        this.register = registry;
        SourceIdGenerator idGenerator = new SourceIdGenerator();
        this.registrationPipelineFactory = new ChannelPipelineFactory() {
            @Override
            public Flux<ChannelPipeline> createPipeline() {
                return Flux.create(fluxSink -> {
                    fluxSink.next(new ChannelPipeline("registrationServer@" + "test",
                            new ServerLoggingChannelHandler(),
                            new ServerHeartbeatHandler(1000 * 30, scheduler),
                            new ServerHandshakeHandler(idGenerator),
                            new RegistrationProcessorHandler(registry)
                    ));
                    fluxSink.complete();
                });
            }
        };

    }

    @Override
    public StreamObserver<WrTy.RegistrationRequest> register(StreamObserver<WrTy.RegistrationResponse> responseObserver) {
        RegistrationSession registrationSession = new RegistrationSession(responseObserver);
        return new StreamObserver<WrTy.RegistrationRequest>() {
            @Override
            public void onNext(WrTy.RegistrationRequest value) {
                registrationSession.onNext(value);
            }

            @Override
            public void onError(Throwable t) {
                registrationSession.onError(t);
            }

            @Override
            public void onCompleted() {
                registrationSession.onCompleted();
            }
        };
    }

    class RegistrationSession {
        private final StreamObserver<WrTy.RegistrationResponse> responseObserver;
        private final ChannelPipeline pipeline;
        private final EmitterProcessor<WrTy.ProtocolMessageEnvelope> registrationSubject = EmitterProcessor.create();
        private final Disposable pipelineSubscription;

        RegistrationSession(StreamObserver<WrTy.RegistrationResponse> responseObserver) {
            this.responseObserver = responseObserver;
            this.pipeline = registrationPipelineFactory.createPipeline().take(1).blockFirst();
            this.pipelineSubscription = connectPipeline();
        }

        void onNext(WrTy.RegistrationRequest registrationRequest) {
            WrTy.ProtocolMessageEnvelope.Builder builder = WrTy.ProtocolMessageEnvelope.newBuilder();
            WrTy.RegistrationRequest.OneofRequestCase kind = registrationRequest.getOneofRequestCase();
            switch (kind) {
                case CLIENTHELLO:
                    WrTy.ClientHello clientHello = registrationRequest.getClientHello();
                    registrationSubject.onNext(builder.setClientHello(clientHello).build());
                    break;
                case HEARTBEAT:
                    registrationSubject.onNext(builder.setHeartbeat(registrationRequest.getHeartbeat()).build());
                    break;
                case INSTANCEINFO:
                    registrationSubject.onNext(builder.setInstanceInfo(registrationRequest.getInstanceInfo()).build());
                    break;
                default:
                    logger.error("Unrecognized registration request message protocol type {}", kind);
                    onError(new IOException("Unrecognized registration request message protocol type " + kind));
            }
        }

        void onError(Throwable t) {
            registrationSubject.onError(t);
            pipelineSubscription.dispose();
        }

        void onCompleted() {
            registrationSubject.onComplete();
            pipelineSubscription.dispose();
        }

        private Disposable connectPipeline() {
            return pipeline.getFirst().handle(registrationSubject)
                    .doOnCancel(() -> logger.debug("Registration pipeline cancel"))
                    .subscribe(
                            next -> {
                                WrTy.ProtocolMessageEnvelope.MessageOneOfCase kind = next.getMessageOneOfCase();
                                WrTy.RegistrationResponse.Builder builder = WrTy.RegistrationResponse.newBuilder();
                                switch (kind) {
                                    case SERVERHELLO:
                                        responseObserver.onNext(builder.setServerHello(next.getServerHello()).build());
                                        break;
                                    case HEARTBEAT:
                                        responseObserver.onNext(builder.setHeartbeat(next.getHeartbeat()).build());
                                        break;
                                    case INSTANCEINFO:
                                        responseObserver.onNext(builder.setAck(next.getAcknowledgement()).build());
                                        break;
                                    default:
                                        onError(new IOException("message protocol type error" + kind));
                                }
                            },
                            e -> {
                                logger.debug("Send onError to transport ({})", e.getMessage());
                                responseObserver.onError(e);
                            },
                            () -> {
                                logger.debug("Send onCompleted to transport ({})");
                                responseObserver.onCompleted();
                            }
                    );
        }
    }
}
