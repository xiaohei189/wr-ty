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
public final class RegistrationServiceGrpc {

  private RegistrationServiceGrpc() {}

  public static final String SERVICE_NAME = "com.xh.demo.grpc.RegistrationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.RegistrationRequest,
      com.xh.demo.grpc.WrTy.RegistrationResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Register",
      requestType = com.xh.demo.grpc.WrTy.RegistrationRequest.class,
      responseType = com.xh.demo.grpc.WrTy.RegistrationResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.RegistrationRequest,
      com.xh.demo.grpc.WrTy.RegistrationResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<com.xh.demo.grpc.WrTy.RegistrationRequest, com.xh.demo.grpc.WrTy.RegistrationResponse> getRegisterMethod;
    if ((getRegisterMethod = RegistrationServiceGrpc.getRegisterMethod) == null) {
      synchronized (RegistrationServiceGrpc.class) {
        if ((getRegisterMethod = RegistrationServiceGrpc.getRegisterMethod) == null) {
          RegistrationServiceGrpc.getRegisterMethod = getRegisterMethod = 
              io.grpc.MethodDescriptor.<com.xh.demo.grpc.WrTy.RegistrationRequest, com.xh.demo.grpc.WrTy.RegistrationResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "com.xh.demo.grpc.RegistrationService", "Register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.RegistrationRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.xh.demo.grpc.WrTy.RegistrationResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new RegistrationServiceMethodDescriptorSupplier("Register"))
                  .build();
          }
        }
     }
     return getRegisterMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegistrationServiceStub newStub(io.grpc.Channel channel) {
    return new RegistrationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegistrationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RegistrationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegistrationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RegistrationServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class RegistrationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.RegistrationRequest> register(
        io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.RegistrationResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getRegisterMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.xh.demo.grpc.WrTy.RegistrationRequest,
                com.xh.demo.grpc.WrTy.RegistrationResponse>(
                  this, METHODID_REGISTER)))
          .build();
    }
  }

  /**
   */
  public static final class RegistrationServiceStub extends io.grpc.stub.AbstractStub<RegistrationServiceStub> {
    private RegistrationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistrationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistrationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistrationServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.RegistrationRequest> register(
        io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.RegistrationResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class RegistrationServiceBlockingStub extends io.grpc.stub.AbstractStub<RegistrationServiceBlockingStub> {
    private RegistrationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistrationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistrationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistrationServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class RegistrationServiceFutureStub extends io.grpc.stub.AbstractStub<RegistrationServiceFutureStub> {
    private RegistrationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistrationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistrationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistrationServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_REGISTER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegistrationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegistrationServiceImplBase serviceImpl, int methodId) {
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
        case METHODID_REGISTER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.register(
              (io.grpc.stub.StreamObserver<com.xh.demo.grpc.WrTy.RegistrationResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RegistrationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegistrationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.xh.demo.grpc.WrTy.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RegistrationService");
    }
  }

  private static final class RegistrationServiceFileDescriptorSupplier
      extends RegistrationServiceBaseDescriptorSupplier {
    RegistrationServiceFileDescriptorSupplier() {}
  }

  private static final class RegistrationServiceMethodDescriptorSupplier
      extends RegistrationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegistrationServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (RegistrationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegistrationServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .build();
        }
      }
    }
    return result;
  }
}
