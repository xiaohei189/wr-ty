package com.wr.ty.grpc.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xiaohei
 * @date 2020/2/29 21:27
 */
public class DefaultPipelineNameGenerator implements PipelineNameGenerator {
    AtomicLong auto = new AtomicLong(0);

    @Override
    public String generate(String name) {
        return auto.incrementAndGet() + " " + name +" ";
    }
}
