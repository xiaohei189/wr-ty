package com.wr.ty.grpc.service;

import com.xh.demo.grpc.SubscribeServiceGrpc;
import com.xh.demo.grpc.SubscribeServiceGrpc.SubscribeServiceImplBase;
import com.xh.demo.grpc.WrTy;
import io.grpc.stub.StreamObserver;

/**
 * @author xiaohei
 * @date 2020/2/24 13:41
 */
public class SubscribeServerImpl extends SubscribeServiceImplBase {
    @Override
    public StreamObserver<WrTy.SubscribeRequest> subscribe(StreamObserver<WrTy.SubscribeResponse> responseObserver) {
        // todo wait to implement
        return super.subscribe(responseObserver);
    }
}
