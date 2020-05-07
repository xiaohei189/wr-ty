package com.wr.ty.grpc.client;

import com.wr.ty.grpc.interest.Interest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * @author xiaohei
 * @date 2020/3/12 1:18
 */
public class DiscoveryClientImpl implements DiscoveryClient {

    private Channel channel;
    private final List<Interest> interests;

    /**
     * @param interests interest service
     */
    public DiscoveryClientImpl(Channel channel, List<Interest> interests) {
        this.channel = channel;
        this.interests = interests;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        return null;
    }

    @Override
    public List<String> getServices() {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
