package com.kongzhong.demo.interceptor;

import com.kongzhong.mrpc.client.RpcClient;
import com.kongzhong.mrpc.demo.service.UserService;

/**
 * @author biezhi
 *         2017/4/19
 */
public class IClientApplication {
    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();

        UserService userService = rpcClient.getProxyBean(UserService.class);
        System.out.println(userService.add(10, 20));
        rpcClient.stop();
    }
}
