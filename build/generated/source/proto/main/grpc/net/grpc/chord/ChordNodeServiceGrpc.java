package net.grpc.chord;

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
  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.NotifyRequest,
      net.grpc.chord.NotifyResponse> getNotifyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Notify",
      requestType = net.grpc.chord.NotifyRequest.class,
      responseType = net.grpc.chord.NotifyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.NotifyRequest,
      net.grpc.chord.NotifyResponse> getNotifyMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.NotifyRequest, net.grpc.chord.NotifyResponse> getNotifyMethod;
    if ((getNotifyMethod = ChordNodeServiceGrpc.getNotifyMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getNotifyMethod = ChordNodeServiceGrpc.getNotifyMethod) == null) {
          ChordNodeServiceGrpc.getNotifyMethod = getNotifyMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.NotifyRequest, net.grpc.chord.NotifyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Notify"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.NotifyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.NotifyResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Notify"))
                  .build();
          }
        }
     }
     return getNotifyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.FindSuccessorRequest,
      net.grpc.chord.FindSuccessorResponse> getFindSuccessorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FindSuccessor",
      requestType = net.grpc.chord.FindSuccessorRequest.class,
      responseType = net.grpc.chord.FindSuccessorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.FindSuccessorRequest,
      net.grpc.chord.FindSuccessorResponse> getFindSuccessorMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.FindSuccessorRequest, net.grpc.chord.FindSuccessorResponse> getFindSuccessorMethod;
    if ((getFindSuccessorMethod = ChordNodeServiceGrpc.getFindSuccessorMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getFindSuccessorMethod = ChordNodeServiceGrpc.getFindSuccessorMethod) == null) {
          ChordNodeServiceGrpc.getFindSuccessorMethod = getFindSuccessorMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.FindSuccessorRequest, net.grpc.chord.FindSuccessorResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "FindSuccessor"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.FindSuccessorRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.FindSuccessorResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("FindSuccessor"))
                  .build();
          }
        }
     }
     return getFindSuccessorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.InquirePredecessorRequest,
      net.grpc.chord.InquirePredecessorResponse> getInquirePredecessorMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InquirePredecessor",
      requestType = net.grpc.chord.InquirePredecessorRequest.class,
      responseType = net.grpc.chord.InquirePredecessorResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.InquirePredecessorRequest,
      net.grpc.chord.InquirePredecessorResponse> getInquirePredecessorMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.InquirePredecessorRequest, net.grpc.chord.InquirePredecessorResponse> getInquirePredecessorMethod;
    if ((getInquirePredecessorMethod = ChordNodeServiceGrpc.getInquirePredecessorMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getInquirePredecessorMethod = ChordNodeServiceGrpc.getInquirePredecessorMethod) == null) {
          ChordNodeServiceGrpc.getInquirePredecessorMethod = getInquirePredecessorMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.InquirePredecessorRequest, net.grpc.chord.InquirePredecessorResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "InquirePredecessor"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.InquirePredecessorRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.InquirePredecessorResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("InquirePredecessor"))
                  .build();
          }
        }
     }
     return getInquirePredecessorMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.PingRequest,
      net.grpc.chord.PingResponse> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Ping",
      requestType = net.grpc.chord.PingRequest.class,
      responseType = net.grpc.chord.PingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.PingRequest,
      net.grpc.chord.PingResponse> getPingMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.PingRequest, net.grpc.chord.PingResponse> getPingMethod;
    if ((getPingMethod = ChordNodeServiceGrpc.getPingMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getPingMethod = ChordNodeServiceGrpc.getPingMethod) == null) {
          ChordNodeServiceGrpc.getPingMethod = getPingMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.PingRequest, net.grpc.chord.PingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Ping"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.PingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.PingResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Ping"))
                  .build();
          }
        }
     }
     return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.TransferDataRequest,
      net.grpc.chord.TransferDataResponse> getTransferDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "TransferData",
      requestType = net.grpc.chord.TransferDataRequest.class,
      responseType = net.grpc.chord.TransferDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.TransferDataRequest,
      net.grpc.chord.TransferDataResponse> getTransferDataMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.TransferDataRequest, net.grpc.chord.TransferDataResponse> getTransferDataMethod;
    if ((getTransferDataMethod = ChordNodeServiceGrpc.getTransferDataMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getTransferDataMethod = ChordNodeServiceGrpc.getTransferDataMethod) == null) {
          ChordNodeServiceGrpc.getTransferDataMethod = getTransferDataMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.TransferDataRequest, net.grpc.chord.TransferDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "TransferData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.TransferDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.TransferDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("TransferData"))
                  .build();
          }
        }
     }
     return getTransferDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.PutRequest,
      net.grpc.chord.PutResponse> getPutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Put",
      requestType = net.grpc.chord.PutRequest.class,
      responseType = net.grpc.chord.PutResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.PutRequest,
      net.grpc.chord.PutResponse> getPutMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.PutRequest, net.grpc.chord.PutResponse> getPutMethod;
    if ((getPutMethod = ChordNodeServiceGrpc.getPutMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getPutMethod = ChordNodeServiceGrpc.getPutMethod) == null) {
          ChordNodeServiceGrpc.getPutMethod = getPutMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.PutRequest, net.grpc.chord.PutResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Put"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.PutRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.PutResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Put"))
                  .build();
          }
        }
     }
     return getPutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.GetRequest,
      net.grpc.chord.GetResponse> getGetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Get",
      requestType = net.grpc.chord.GetRequest.class,
      responseType = net.grpc.chord.GetResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.GetRequest,
      net.grpc.chord.GetResponse> getGetMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.GetRequest, net.grpc.chord.GetResponse> getGetMethod;
    if ((getGetMethod = ChordNodeServiceGrpc.getGetMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getGetMethod = ChordNodeServiceGrpc.getGetMethod) == null) {
          ChordNodeServiceGrpc.getGetMethod = getGetMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.GetRequest, net.grpc.chord.GetResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.GetRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.GetResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Get"))
                  .build();
          }
        }
     }
     return getGetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.InquireSuccessorsListRequest,
      net.grpc.chord.InquireSuccessorsListResponse> getInquireSuccessorsListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "InquireSuccessorsList",
      requestType = net.grpc.chord.InquireSuccessorsListRequest.class,
      responseType = net.grpc.chord.InquireSuccessorsListResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.InquireSuccessorsListRequest,
      net.grpc.chord.InquireSuccessorsListResponse> getInquireSuccessorsListMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.InquireSuccessorsListRequest, net.grpc.chord.InquireSuccessorsListResponse> getInquireSuccessorsListMethod;
    if ((getInquireSuccessorsListMethod = ChordNodeServiceGrpc.getInquireSuccessorsListMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getInquireSuccessorsListMethod = ChordNodeServiceGrpc.getInquireSuccessorsListMethod) == null) {
          ChordNodeServiceGrpc.getInquireSuccessorsListMethod = getInquireSuccessorsListMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.InquireSuccessorsListRequest, net.grpc.chord.InquireSuccessorsListResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "InquireSuccessorsList"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.InquireSuccessorsListRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.InquireSuccessorsListResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("InquireSuccessorsList"))
                  .build();
          }
        }
     }
     return getInquireSuccessorsListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<net.grpc.chord.LeaveRequest,
      net.grpc.chord.LeaveResponse> getLeaveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Leave",
      requestType = net.grpc.chord.LeaveRequest.class,
      responseType = net.grpc.chord.LeaveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<net.grpc.chord.LeaveRequest,
      net.grpc.chord.LeaveResponse> getLeaveMethod() {
    io.grpc.MethodDescriptor<net.grpc.chord.LeaveRequest, net.grpc.chord.LeaveResponse> getLeaveMethod;
    if ((getLeaveMethod = ChordNodeServiceGrpc.getLeaveMethod) == null) {
      synchronized (ChordNodeServiceGrpc.class) {
        if ((getLeaveMethod = ChordNodeServiceGrpc.getLeaveMethod) == null) {
          ChordNodeServiceGrpc.getLeaveMethod = getLeaveMethod = 
              io.grpc.MethodDescriptor.<net.grpc.chord.LeaveRequest, net.grpc.chord.LeaveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "chord.ChordNodeService", "Leave"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.LeaveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  net.grpc.chord.LeaveResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new ChordNodeServiceMethodDescriptorSupplier("Leave"))
                  .build();
          }
        }
     }
     return getLeaveMethod;
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
    public void notify(net.grpc.chord.NotifyRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.NotifyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNotifyMethod(), responseObserver);
    }

    /**
     */
    public void findSuccessor(net.grpc.chord.FindSuccessorRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.FindSuccessorResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFindSuccessorMethod(), responseObserver);
    }

    /**
     */
    public void inquirePredecessor(net.grpc.chord.InquirePredecessorRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.InquirePredecessorResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInquirePredecessorMethod(), responseObserver);
    }

    /**
     */
    public void ping(net.grpc.chord.PingRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.PingResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     */
    public void transferData(net.grpc.chord.TransferDataRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.TransferDataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getTransferDataMethod(), responseObserver);
    }

    /**
     */
    public void put(net.grpc.chord.PutRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.PutResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPutMethod(), responseObserver);
    }

    /**
     */
    public void get(net.grpc.chord.GetRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.GetResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    /**
     */
    public void inquireSuccessorsList(net.grpc.chord.InquireSuccessorsListRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.InquireSuccessorsListResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInquireSuccessorsListMethod(), responseObserver);
    }

    /**
     */
    public void leave(net.grpc.chord.LeaveRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.LeaveResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLeaveMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getNotifyMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.NotifyRequest,
                net.grpc.chord.NotifyResponse>(
                  this, METHODID_NOTIFY)))
          .addMethod(
            getFindSuccessorMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.FindSuccessorRequest,
                net.grpc.chord.FindSuccessorResponse>(
                  this, METHODID_FIND_SUCCESSOR)))
          .addMethod(
            getInquirePredecessorMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.InquirePredecessorRequest,
                net.grpc.chord.InquirePredecessorResponse>(
                  this, METHODID_INQUIRE_PREDECESSOR)))
          .addMethod(
            getPingMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.PingRequest,
                net.grpc.chord.PingResponse>(
                  this, METHODID_PING)))
          .addMethod(
            getTransferDataMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.TransferDataRequest,
                net.grpc.chord.TransferDataResponse>(
                  this, METHODID_TRANSFER_DATA)))
          .addMethod(
            getPutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.PutRequest,
                net.grpc.chord.PutResponse>(
                  this, METHODID_PUT)))
          .addMethod(
            getGetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.GetRequest,
                net.grpc.chord.GetResponse>(
                  this, METHODID_GET)))
          .addMethod(
            getInquireSuccessorsListMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.InquireSuccessorsListRequest,
                net.grpc.chord.InquireSuccessorsListResponse>(
                  this, METHODID_INQUIRE_SUCCESSORS_LIST)))
          .addMethod(
            getLeaveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                net.grpc.chord.LeaveRequest,
                net.grpc.chord.LeaveResponse>(
                  this, METHODID_LEAVE)))
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
    public void notify(net.grpc.chord.NotifyRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.NotifyResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getNotifyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void findSuccessor(net.grpc.chord.FindSuccessorRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.FindSuccessorResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFindSuccessorMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void inquirePredecessor(net.grpc.chord.InquirePredecessorRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.InquirePredecessorResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInquirePredecessorMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(net.grpc.chord.PingRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.PingResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void transferData(net.grpc.chord.TransferDataRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.TransferDataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getTransferDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void put(net.grpc.chord.PutRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.PutResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getPutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void get(net.grpc.chord.GetRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.GetResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void inquireSuccessorsList(net.grpc.chord.InquireSuccessorsListRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.InquireSuccessorsListResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInquireSuccessorsListMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void leave(net.grpc.chord.LeaveRequest request,
        io.grpc.stub.StreamObserver<net.grpc.chord.LeaveResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request, responseObserver);
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
    public net.grpc.chord.NotifyResponse notify(net.grpc.chord.NotifyRequest request) {
      return blockingUnaryCall(
          getChannel(), getNotifyMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.FindSuccessorResponse findSuccessor(net.grpc.chord.FindSuccessorRequest request) {
      return blockingUnaryCall(
          getChannel(), getFindSuccessorMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.InquirePredecessorResponse inquirePredecessor(net.grpc.chord.InquirePredecessorRequest request) {
      return blockingUnaryCall(
          getChannel(), getInquirePredecessorMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.PingResponse ping(net.grpc.chord.PingRequest request) {
      return blockingUnaryCall(
          getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.TransferDataResponse transferData(net.grpc.chord.TransferDataRequest request) {
      return blockingUnaryCall(
          getChannel(), getTransferDataMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.PutResponse put(net.grpc.chord.PutRequest request) {
      return blockingUnaryCall(
          getChannel(), getPutMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.GetResponse get(net.grpc.chord.GetRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.InquireSuccessorsListResponse inquireSuccessorsList(net.grpc.chord.InquireSuccessorsListRequest request) {
      return blockingUnaryCall(
          getChannel(), getInquireSuccessorsListMethod(), getCallOptions(), request);
    }

    /**
     */
    public net.grpc.chord.LeaveResponse leave(net.grpc.chord.LeaveRequest request) {
      return blockingUnaryCall(
          getChannel(), getLeaveMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.NotifyResponse> notify(
        net.grpc.chord.NotifyRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getNotifyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.FindSuccessorResponse> findSuccessor(
        net.grpc.chord.FindSuccessorRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFindSuccessorMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.InquirePredecessorResponse> inquirePredecessor(
        net.grpc.chord.InquirePredecessorRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInquirePredecessorMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.PingResponse> ping(
        net.grpc.chord.PingRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.TransferDataResponse> transferData(
        net.grpc.chord.TransferDataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getTransferDataMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.PutResponse> put(
        net.grpc.chord.PutRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getPutMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.GetResponse> get(
        net.grpc.chord.GetRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.InquireSuccessorsListResponse> inquireSuccessorsList(
        net.grpc.chord.InquireSuccessorsListRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInquireSuccessorsListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.grpc.chord.LeaveResponse> leave(
        net.grpc.chord.LeaveRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLeaveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_NOTIFY = 0;
  private static final int METHODID_FIND_SUCCESSOR = 1;
  private static final int METHODID_INQUIRE_PREDECESSOR = 2;
  private static final int METHODID_PING = 3;
  private static final int METHODID_TRANSFER_DATA = 4;
  private static final int METHODID_PUT = 5;
  private static final int METHODID_GET = 6;
  private static final int METHODID_INQUIRE_SUCCESSORS_LIST = 7;
  private static final int METHODID_LEAVE = 8;

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
          serviceImpl.notify((net.grpc.chord.NotifyRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.NotifyResponse>) responseObserver);
          break;
        case METHODID_FIND_SUCCESSOR:
          serviceImpl.findSuccessor((net.grpc.chord.FindSuccessorRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.FindSuccessorResponse>) responseObserver);
          break;
        case METHODID_INQUIRE_PREDECESSOR:
          serviceImpl.inquirePredecessor((net.grpc.chord.InquirePredecessorRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.InquirePredecessorResponse>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((net.grpc.chord.PingRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.PingResponse>) responseObserver);
          break;
        case METHODID_TRANSFER_DATA:
          serviceImpl.transferData((net.grpc.chord.TransferDataRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.TransferDataResponse>) responseObserver);
          break;
        case METHODID_PUT:
          serviceImpl.put((net.grpc.chord.PutRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.PutResponse>) responseObserver);
          break;
        case METHODID_GET:
          serviceImpl.get((net.grpc.chord.GetRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.GetResponse>) responseObserver);
          break;
        case METHODID_INQUIRE_SUCCESSORS_LIST:
          serviceImpl.inquireSuccessorsList((net.grpc.chord.InquireSuccessorsListRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.InquireSuccessorsListResponse>) responseObserver);
          break;
        case METHODID_LEAVE:
          serviceImpl.leave((net.grpc.chord.LeaveRequest) request,
              (io.grpc.stub.StreamObserver<net.grpc.chord.LeaveResponse>) responseObserver);
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
      return net.grpc.chord.ChordNodeProto.getDescriptor();
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
              .addMethod(getInquirePredecessorMethod())
              .addMethod(getPingMethod())
              .addMethod(getTransferDataMethod())
              .addMethod(getPutMethod())
              .addMethod(getGetMethod())
              .addMethod(getInquireSuccessorsListMethod())
              .addMethod(getLeaveMethod())
              .build();
        }
      }
    }
    return result;
  }
}
