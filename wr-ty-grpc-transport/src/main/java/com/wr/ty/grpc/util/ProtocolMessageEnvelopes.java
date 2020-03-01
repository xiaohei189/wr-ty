package com.wr.ty.grpc.util;


import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/13 14:34
 */
public class ProtocolMessageEnvelopes {

    public static WrTy.ProtocolMessageEnvelope HEART_BEAT = WrTy.ProtocolMessageEnvelope.newBuilder().setHeartbeat(WrTy.Heartbeat.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope CLIENT_HELLO = WrTy.ProtocolMessageEnvelope.newBuilder().setClientHello(WrTy.ClientHello.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope SERVER_HELLO = WrTy.ProtocolMessageEnvelope.newBuilder().setServerHello(WrTy.ServerHello.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope DEFAULT_INSTANCE = WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(WrTy.InstanceInfo.getDefaultInstance()).build();

    public static WrTy.ProtocolMessageEnvelope fromClientHello(WrTy.ClientHello clientHello) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setClientHello(clientHello).build();
        return messageEnvelope;
    }

    public static WrTy.ProtocolMessageEnvelope fromServerHello(WrTy.ServerHello value) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setServerHello(value).build();
        return messageEnvelope;
    }

    public static WrTy.ProtocolMessageEnvelope fromHeartbeat(WrTy.Heartbeat heartbeat) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setHeartbeat(heartbeat).build();
        return messageEnvelope;
    }

    public static WrTy.ProtocolMessageEnvelope fromInstance(WrTy.InstanceInfo value) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(value).build();
        return messageEnvelope;
    }

    public static WrTy.ProtocolMessageEnvelope fromInterestRegistration(WrTy.InterestRegistration value) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setInterestRegistration(value).build();
        return messageEnvelope;
    }

    public static WrTy.ProtocolMessageEnvelope createSubscribeAllRegistration() {
        WrTy.InterestRegistration interestRegistration = WrTy.InterestRegistration.newBuilder()
                .addInterests(WrTy.Interest.newBuilder().setAll(WrTy.Interest.AllInterest.getDefaultInstance())).build();
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setInterestRegistration(interestRegistration).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse convertServerHello(WrTy.ServerHello value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setServerHello(value).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse convertHeartbeat(WrTy.Heartbeat value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setHeartbeat(value).build();
        return messageEnvelope;
    }

    public static WrTy.SubscribeResponse convertChangeNotification(WrTy.ChangeNotification value) {
        WrTy.SubscribeResponse messageEnvelope = WrTy.SubscribeResponse.newBuilder().setChangeNotification(value).build();
        return messageEnvelope;
    }


    public static WrTy.ProtocolMessageEnvelope fromChangeNotification(WrTy.ChangeNotification value) {
        WrTy.ProtocolMessageEnvelope messageEnvelope = WrTy.ProtocolMessageEnvelope.newBuilder().setChangeNotification(value).build();
        return messageEnvelope;
    }
}
