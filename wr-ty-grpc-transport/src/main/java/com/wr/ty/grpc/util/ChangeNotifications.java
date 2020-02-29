package com.wr.ty.grpc.util;

import com.xh.demo.grpc.WrTy;

/**
 * @author xiaohei
 * @date 2020/2/22 15:12
 */
public class ChangeNotifications {
    public static WrTy.ChangeNotification newAddNotification(WrTy.InstanceInfo instanceInfo) {
        WrTy.ChangeNotification.AddChangeNotification addInstance = WrTy.ChangeNotification.AddChangeNotification.newBuilder()
                .setInstanceInfo(instanceInfo).build();
        return WrTy.ChangeNotification.newBuilder().setAdd(addInstance).build();
    }

    public static WrTy.ChangeNotification newDeleteNotification(WrTy.InstanceInfo instanceInfo) {
        WrTy.ChangeNotification.DeleteChangeNotification deleteInstance = WrTy.ChangeNotification.DeleteChangeNotification.newBuilder()
                .setInstanceId(instanceInfo.getId()).build();
        return WrTy.ChangeNotification.newBuilder().setDelete(deleteInstance).build();
    }
}
