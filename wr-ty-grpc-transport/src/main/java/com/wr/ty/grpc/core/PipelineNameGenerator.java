package com.wr.ty.grpc.core;

/**
 * @author xiaohei
 * @date 2020/2/29 21:26
 */
@FunctionalInterface
public interface PipelineNameGenerator {
    String generate(String name);
}
