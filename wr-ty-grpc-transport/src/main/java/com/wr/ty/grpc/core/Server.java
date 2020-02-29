package com.wr.ty.grpc.core;

/**
 * @author xiaohei
 * @date 2020/2/20 23:00
 */
public class Server {

    private String hostName;
    private int port;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
