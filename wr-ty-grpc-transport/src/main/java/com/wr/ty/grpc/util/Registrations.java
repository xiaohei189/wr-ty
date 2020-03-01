package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/29 21:05
 */
public class Registrations {

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
