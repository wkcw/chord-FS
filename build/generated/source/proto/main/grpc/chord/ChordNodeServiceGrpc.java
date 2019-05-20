package chord;

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
    value = "by gRPC proto compiler (version 1.20.0)",
    comments = "Source: chordNodeService.proto")
public final class ChordNodeServiceGrpc {

  private ChordNodeServiceGrpc() {}

  public static final String SERVICE_NAME = "chord.ChordNodeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.Identifier,
      chord.ChordNodeServiceOuterClass.NotifyResponse> getNotifyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Notify",
      requestType = chord.ChordNodeServiceOuterClass.Identifier.class,
      responseType = chord.ChordNodeServiceOuterClass.NotifyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.Identifier,
      chord.ChordNodeServiceOuterClass.NotifyResponse> getNotifyMethod() {
    io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.Identifier, chord.ChordNodeServiceOuterClass.NotifyResponse> getNotifyMethod;
    if ((getNotifyMethod = ChordNodeServiceGrpc.getNotifyMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getNotifyMethod = ChordNodeServiceGrpc.getNotifyMethod) == null) {
          ChordNodeServiceGrpc.getNotifyMethod = getNotifyMethod = 
              io.grpc.MethodDescriptor.<chord.ChordNodeServiceOuterClass.Identifier, chord.ChordNodeServiceOuterClass.NotifyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Notify"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  chord.ChordNodeServiceOuterClass.Identifier.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  chord.ChordNodeServiceOuterClass.NotifyResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Notify"))
                  .build();
          }
        }
     }
     return getNotifyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.FindSuccessorRequest,
      chord.ChordNodeServiceOuterClass.FindSuccessorResponse> getFindSuccessorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FindSuccessor",
      requestType = chord.ChordNodeServiceOuterClass.FindSuccessorRequest.class,
      responseType = chord.ChordNodeServiceOuterClass.FindSuccessorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.FindSuccessorRequest,
      chord.ChordNodeServiceOuterClass.FindSuccessorResponse> getFindSuccessorMethod() {
    io.grpc.MethodDescriptor<chord.ChordNodeServiceOuterClass.FindSuccessorRequest, chord.ChordNodeServiceOuterClass.FindSuccessorResponse> getFindSuccessorMethod;
    if ((getFindSuccessorMethod = ChordNodeServiceGrpc.getFindSuccessorMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getFindSuccessorMethod = ChordNodeServiceGrpc.getFindSuccessorMethod) == null) {
          ChordNodeServiceGrpc.getFindSuccessorMethod = getFindSuccessorMethod = 
              io.grpc.MethodDescriptor.<chord.ChordNodeServiceOuterClass.FindSuccessorRequest, chord.ChordNodeServiceOuterClass.FindSuccessorResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "FindSuccessor"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  chord.ChordNodeServiceOuterClass.FindSuccessorRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  chord.ChordNodeServiceOuterClass.FindSuccessorResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("FindSuccessor"))
                  .build();
          }
        }
     }
     return getFindSuccessorMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static ChordNodeServiceStub newStub(io.grpc.Channel channel) {
    return new ChordNodeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static ChordNodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new ChordNodeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static ChordNodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new ChordNodeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class ChordNodeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void notify(chord.ChordNodeServiceOuterClass.Identifier request,
        io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.NotifyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNotifyMethod(), responseObserver);
    }

    /**
     */
    public void findSuccessor(chord.ChordNodeServiceOuterClass.FindSuccessorRequest request,
        io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.FindSuccessorResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFindSuccessorMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getNotifyMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                chord.ChordNodeServiceOuterClass.Identifier,
                chord.ChordNodeServiceOuterClass.NotifyResponse>(
                  this, METHODID_NOTIFY)))
          .addMethod(
            getFindSuccessorMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                chord.ChordNodeServiceOuterClass.FindSuccessorRequest,
                chord.ChordNodeServiceOuterClass.FindSuccessorResponse>(
                  this, METHODID_FIND_SUCCESSOR)))
          .build();
    }
  }

  /**
   */
  public static final class ChordNodeServiceStub extends io.grpc.stub.AbstractStub<ChordNodeServiceStub> {
    private ChordNodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChordNodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChordNodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChordNodeServiceStub(channel, callOptions);
    }

    /**
     */
    public void notify(chord.ChordNodeServiceOuterClass.Identifier request,
        io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.NotifyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getNotifyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findSuccessor(chord.ChordNodeServiceOuterClass.FindSuccessorRequest request,
        io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.FindSuccessorResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindSuccessorMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class ChordNodeServiceBlockingStub extends io.grpc.stub.AbstractStub<ChordNodeServiceBlockingStub> {
    private ChordNodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChordNodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChordNodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChordNodeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public chord.ChordNodeServiceOuterClass.NotifyResponse notify(chord.ChordNodeServiceOuterClass.Identifier request) {
      return blockingUnaryCall(
          getChannel(), getNotifyMethod(), getCallOptions(), request);
    }

    /**
     */
    public chord.ChordNodeServiceOuterClass.FindSuccessorResponse findSuccessor(chord.ChordNodeServiceOuterClass.FindSuccessorRequest request) {
      return blockingUnaryCall(
          getChannel(), getFindSuccessorMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class ChordNodeServiceFutureStub extends io.grpc.stub.AbstractStub<ChordNodeServiceFutureStub> {
    private ChordNodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private ChordNodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected ChordNodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new ChordNodeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<chord.ChordNodeServiceOuterClass.NotifyResponse> notify(
        chord.ChordNodeServiceOuterClass.Identifier request) {
      return futureUnaryCall(
          getChannel().newCall(getNotifyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<chord.ChordNodeServiceOuterClass.FindSuccessorResponse> findSuccessor(
        chord.ChordNodeServiceOuterClass.FindSuccessorRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFindSuccessorMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_NOTIFY = 0;
  private static final int METHODID_FIND_SUCCESSOR = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final ChordNodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(ChordNodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_NOTIFY:
          serviceImpl.notify((chord.ChordNodeServiceOuterClass.Identifier) request,
              (io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.NotifyResponse>) responseObserver);
          break;
        case METHODID_FIND_SUCCESSOR:
          serviceImpl.findSuccessor((chord.ChordNodeServiceOuterClass.FindSuccessorRequest) request,
              (io.grpc.stub.StreamObserver<chord.ChordNodeServiceOuterClass.FindSuccessorResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class ChordNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    ChordNodeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return chord.ChordNodeServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("ChordNodeService");
    }
  }

  private static final class ChordNodeServiceFileDescriptorSupplier
      extends ChordNodeServiceBaseDescriptorSupplier {
    ChordNodeServiceFileDescriptorSupplier() {}
  }

  private static final class ChordNodeServiceMethodDescriptorSupplier
      extends ChordNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    ChordNodeServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (ChordNodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new ChordNodeServiceFileDescriptorSupplier())
              .addMethod(getNotifyMethod())
              .addMethod(getFindSuccessorMethod())
              .build();
        }
      }
    }
    return result;
  }
}
