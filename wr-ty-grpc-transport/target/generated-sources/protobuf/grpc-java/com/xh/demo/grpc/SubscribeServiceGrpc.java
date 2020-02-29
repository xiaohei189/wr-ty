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
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.21.0)",
    comments = "Source: wr_ty.proto")
public final class SubscribeServiceGrpc {

  private SubscribeServiceGrpc() {}

  public static final String SERVICE_NAME = "com.xh.demo.grpc.SubscribeService";

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
    if ((getSubscribeMethod = SubscribeServiceGrpc.getSubscribeMethod) == null) {
      synchronized (SubscribeServiceGrpc.class) {
        if ((getSubscribeMethod = SubscribeServiceGrpc.getSubscribeMethod) == null) {
          SubscribeServiceGrpc.getSubscribeMethod = getSubscribeMethod = 
              io.grpc.MethodDescriptor.<com.xh.demo.grpc.WrTy.SubscribeRequest, com.xh.demo.grpc.WrTy.SubscribeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "com.xh.demo.grpc.SubscribeService", "Subscribe"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.SubscribeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.SubscribeResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new SubscribeServiceMethodDescriptorSupplier("Subscribe"))
                  .build();
          }
        }
     }
     return getSubscribeMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SubscribeServiceStub newStub(io.grpc.Channel channel) {
    return new SubscribeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SubscribeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SubscribeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SubscribeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SubscribeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class SubscribeServiceImplBase implements io.grpc.BindableService {

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
   */
  public static final class SubscribeServiceStub extends io.grpc.stub.AbstractStub<SubscribeServiceStub> {
    private SubscribeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SubscribeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscribeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SubscribeServiceStub(channel, callOptions);
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
   */
  public static final class SubscribeServiceBlockingStub extends io.grpc.stub.AbstractStub<SubscribeServiceBlockingStub> {
    private SubscribeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SubscribeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscribeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SubscribeServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class SubscribeServiceFutureStub extends io.grpc.stub.AbstractStub<SubscribeServiceFutureStub> {
    private SubscribeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SubscribeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SubscribeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SubscribeServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_SUBSCRIBE = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SubscribeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SubscribeServiceImplBase serviceImpl, int methodId) {
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

  private static abstract class SubscribeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SubscribeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.xh.demo.grpc.WrTy.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SubscribeService");
    }
  }

  private static final class SubscribeServiceFileDescriptorSupplier
      extends SubscribeServiceBaseDescriptorSupplier {
    SubscribeServiceFileDescriptorSupplier() {}
  }

  private static final class SubscribeServiceMethodDescriptorSupplier
      extends SubscribeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SubscribeServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (SubscribeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SubscribeServiceFileDescriptorSupplier())
              .addMethod(getSubscribeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
