package com.wr.ty.grpc.service;

import com.xh.demo.grpc.ReplicationServiceGrpc;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;

/**
 * @author xiaohei
 * @date 2020/2/29 17:48
 */
public class ReplicationServerImpl extends ReplicationServiceGrpc.ReplicationServiceImplBase {
    @Override
    public StreamObserver<WrTy.SubscribeRequest> subscribe(StreamObserver<WrTy.SubscribeResponse> responseObserver) {
        //todo wait to completed
        return super.subscribe(responseObserver);
    }
}
