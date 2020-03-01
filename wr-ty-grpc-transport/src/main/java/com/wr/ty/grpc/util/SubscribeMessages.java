package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/3/1 15:32
 */
public class SubscribeMessages {

    public static final WrTy.SubscribeRequest CLIENT_HELLO = WrTy.SubscribeRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
    public static final WrTy.SubscribeRequest CLIENT_HEART = WrTy.SubscribeRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
    public static final WrTy.SubscribeRequest INTEREST_REGISTRATION = WrTy.SubscribeRequest.newBuilder().setInterestRegistration(WrTy.InterestRegistration.getDefaultInstance()).build();
    public static final WrTy.SubscribeResponse SERVER_HELLO = WrTy.SubscribeResponse.newBuilder().setServerHello(WrTy.ServerHello.getDefaultInstance()).build();
    public static final WrTy.SubscribeResponse SERVER_HEART = WrTy.SubscribeResponse.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
}
