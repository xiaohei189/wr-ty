package com.wr.ty.grpc.util;


import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/13 14:34
 */
public class ProtocolMessageEnvelopes {

    public static WrTy.ProtocolMessageEnvelope HEART_BEAT = WrTy.ProtocolMessageEnvelope.newBuilder().setHeartbeat(WrTy.Heartbeat.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope HEART_BEAT_REPLY = WrTy.ProtocolMessageEnvelope.newBuilder().setHeartbeatReply(WrTy.HeartbeatReply.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope CLIENT_HELLO = WrTy.ProtocolMessageEnvelope.newBuilder().setClientHello(WrTy.ClientHello.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope SERVER_HELLO = WrTy.ProtocolMessageEnvelope.newBuilder().setServerHello(WrTy.ServerHello.newBuilder().build()).build();
    public static WrTy.ProtocolMessageEnvelope DEFAULT_INSTANCE = WrTy.ProtocolMessageEnvelope.newBuilder().setInstanceInfo(WrTy.InstanceInfo.getDefaultInstance()).build();


}
