package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/20 17:34
 */
public class RegistrationMessages {

    public static final WrTy.RegistrationRequest CLIENT_HELLO = WrTy.RegistrationRequest.newBuilder().setClientHello(WrTy.ClientHello.getDefaultInstance()).build();
    public static final WrTy.RegistrationRequest CLIENT_HEART = WrTy.RegistrationRequest.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
    public static final WrTy.RegistrationRequest INSTANCE = WrTy.RegistrationRequest.newBuilder().setInstanceInfo(WrTy.InstanceInfo.getDefaultInstance()).build();
    public static final WrTy.RegistrationResponse SERVER_HELLO = WrTy.RegistrationResponse.newBuilder().setServerHello(WrTy.ServerHello.getDefaultInstance()).build();
    public static final WrTy.RegistrationResponse SERVER_HEART = WrTy.RegistrationResponse.newBuilder().setHeartbeat(WrTy.Heartbeat.getDefaultInstance()).build();
    public static final WrTy.RegistrationResponse ACK = WrTy.RegistrationResponse.newBuilder().setAck(WrTy.Acknowledgement.getDefaultInstance()).build();

    public static WrTy.RegistrationResponse convertServerHello(WrTy.ServerHello value) {
        WrTy.RegistrationResponse messageEnvelope = WrTy.RegistrationResponse.newBuilder().setServerHello(value).build();
        return messageEnvelope;
    }

    public static WrTy.RegistrationResponse convertHeartbeat(WrTy.Heartbeat value) {
        WrTy.RegistrationResponse messageEnvelope = WrTy.RegistrationResponse.newBuilder().setHeartbeat(value).build();
        return messageEnvelope;
    }

    public static WrTy.RegistrationResponse convertAck(WrTy.InstanceInfo value) {
        WrTy.RegistrationResponse messageEnvelope = WrTy.RegistrationResponse.newBuilder().setAck(WrTy.Acknowledgement.getDefaultInstance()).build();
        return messageEnvelope;
    }
}
