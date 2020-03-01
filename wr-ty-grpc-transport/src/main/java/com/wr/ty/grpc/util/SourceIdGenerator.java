package com.wr.ty.grpc.util;


import com.xh.demo.grpc.WrTy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @deprecated
 * @see com.wr.ty.grpc.core.PipelineNameGenerator
 */
public class SourceIdGenerator {

    private final AtomicLong idRef = new AtomicLong(0);


}
