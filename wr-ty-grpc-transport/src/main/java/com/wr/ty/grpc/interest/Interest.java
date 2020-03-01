package com.wr.ty.grpc.interest;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/13 13:18
 */
public interface Interest {

    enum Operator {Equals, Like}

    Operator getOperator();

    String getPattern();

    boolean matches(WrTy.InstanceInfo instanceInfo);

    boolean isAtomicInterest();
}
