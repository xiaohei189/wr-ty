package com.wr.ty.grpc.interest;

import com.xh.demo.grpc.WrTy;

/**
 * @author weirui (xiao hei)
 * @date 2019/4/7 15:42
 */
public interface Interest {

    enum Operator {Equals, Like}

    Operator getOperator();

    String getPattern();

    boolean matches(WrTy.InstanceInfo instanceInfo);

    boolean isAtomicInterest();
}
