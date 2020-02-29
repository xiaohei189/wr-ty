package com.xh.demo.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 **
 * special SubscribeService only Subscribe all
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.21.0)",
    comments = "Source: wr_ty.proto")
public final class ReplicationServiceGrpc {

  private ReplicationServiceGrpc() {}

  public static final String SERVICE_NAME = "com.xh.demo.grpc.ReplicationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.SubscribeRequest,
      com.xh.demo.grpc.WrTy.SubscribeResponse> getSubscribeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Subscribe",
      requestType = com.xh.demo.grpc.WrTy.SubscribeRequest.class,
      responseType = com.xh.demo.grpc.WrTy.SubscribeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.SubscribeRequest,
      com.xh.demo.grpc.WrTy.SubscribeResponse> getSubscribeMethod() {
    io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.SubscribeRequest, com.xh.demo.grpc.WrTy.SubscribeResponse> getSubscribeMethod;
    if ((getSubscribeMethod = ReplicationServiceGrpc.getSubscribeMethod) == null) {
      synchronized (ReplicationServiceGrpc.class) {
        if ((getSubscribeMethod = ReplicationServiceGrpc.getSubscribeMethod) == null) {
          ReplicationServiceGrpc.getSubscribeMethod = getSubscribeMethod = 
              io.grpc.MethodDescriptor.<com.xh.demo.grpc.WrTy.SubscribeRequest, com.xh.demo.grpc.WrTy.SubscribeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "com.xh.demo.grpc.ReplicationService", "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.SubscribeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ReplicationServiceMethodDescriptorSupplier("Subscribe"))
                  .build();
          }
        }
     }
     return getSubscribeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ReplicationServiceStub newStub(io.grpc.Channel channel) {
    return new ReplicationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ReplicationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ReplicationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ReplicationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ReplicationServiceFutureStub(channel);
  }

  /**
   * <pre>
   **
   * special SubscribeService only Subscribe all
   * </pre>
   */
  public static abstract class ReplicationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.SubscribeRequest> subscribe(
        io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.SubscribeResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getSubscribeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSubscribeMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.xh.demo.grpc.WrTy.SubscribeRequest,
                com.xh.demo.grpc.WrTy.SubscribeResponse>(
                  this, METHODID_SUBSCRIBE)))
          .build();
    }
  }

  /**
   * <pre>
   **
   * special SubscribeService only Subscribe all
   * </pre>
   */
  public static final class ReplicationServiceStub extends io.grpc.stub.AbstractStub<ReplicationServiceStub> {
    private ReplicationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicationServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.SubscribeRequest> subscribe(
        io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.SubscribeResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getSubscribeMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * <pre>
   **
   * special SubscribeService only Subscribe all
   * </pre>
   */
  public static final class ReplicationServiceBlockingStub extends io.grpc.stub.AbstractStub<ReplicationServiceBlockingStub> {
    private ReplicationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicationServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * <pre>
   **
   * special SubscribeService only Subscribe all
   * </pre>
   */
  public static final class ReplicationServiceFutureStub extends io.grpc.stub.AbstractStub<ReplicationServiceFutureStub> {
    private ReplicationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ReplicationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ReplicationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ReplicationServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ReplicationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ReplicationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SUBSCRIBE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.subscribe(
              (io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.SubscribeResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ReplicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ReplicationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.xh.demo.grpc.WrTy.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ReplicationService");
    }
  }

  private static final class ReplicationServiceFileDescriptorSupplier
      extends ReplicationServiceBaseDescriptorSupplier {
    ReplicationServiceFileDescriptorSupplier() {}
  }

  private static final class ReplicationServiceMethodDescriptorSupplier
      extends ReplicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ReplicationServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (ReplicationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ReplicationServiceFileDescriptorSupplier())
              .addMethod(getSubscribeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
