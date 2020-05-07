package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/3/1 15:32
 */
public class SubscribeMessages {

    public static final WrTy.SubscribeRequest CLIENT_HELLO = WrTy.SubscribeRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
    public static final WrTy.SubscribeRequest CLIENT_HEART = WrTy.SubscribeRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
    public static final WrTy.SubscribeRequest INTEREST_REGISTRATION = null;
//    WrTy.SubscribeRequest.newBuilder().setInterestRegistration(WrTy.InterestRegistration.getDefaultInstance()).build();
    public static final WrTy.SubscribeResponse SERVER_HELLO = WrTy.SubscribeResponse.newBuilder().setServerHello(WrTy.ServerHello.getDefaultInstance()).build();
    public static final WrTy.SubscribeResponse SERVER_HEART = WrTy.SubscribeResponse.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();

    public static WrTy.SubscribeRequest fromInterestRegistration(WrTy.InterestRegistration value) {
        WrTy.SubscribeRequest messageEnvelope = null;
//        WrTy.SubscribeRequest.newBuilder().setInterestRegistration(value).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse fromServerHello(WrTy.ServerHello value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setServerHello(value).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse fromHeartbeat(WrTy.Heartbeat value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setHeartbeat(value).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse fromChangeNotification(WrTy.ChangeNotification value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setChangeNotification(value).build();
        return messageEnvelope;
    }

}
