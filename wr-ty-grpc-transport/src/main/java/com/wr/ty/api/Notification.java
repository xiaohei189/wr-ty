package com.wr.ty.api;

/**
 * Registry能处理的消息类型
 *
 * @author weirui (xiao hei)
 * @date 2020/5/16 1:37
 */
public interface Notification<T> {
    enum type {
        ADD, UPDATE, DELETE
    }
    /**
     *  返回当前通知数据
     * @return
     */
    T data();
}
