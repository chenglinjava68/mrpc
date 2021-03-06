package com.kongzhong.mrpc.cluster.ha;

import com.kongzhong.mrpc.client.RpcInvoker;
import com.kongzhong.mrpc.cluster.loadblance.LoadBalance;
import com.kongzhong.mrpc.config.DefaultConfig;
import com.kongzhong.mrpc.exception.RpcException;
import com.kongzhong.mrpc.exception.ServiceException;
import com.kongzhong.mrpc.model.RpcRequest;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 失效切换策略
 *
 * @author biezhi
 *         2017/4/24
 */
@Slf4j
public class FailOverHaStrategy implements HaStrategy {

    //确保每个线程持有一份单独的ArrayList<Referer<T>（FailOverHaStrategy，会有单个实例被多个线程调用）
    protected FastThreadLocal<List> referersHolder = new FastThreadLocal<>();

    @Override
    public Object call(RpcRequest request, LoadBalance loadBalance) throws Throwable {
        int rc = DefaultConfig.serviceRecryCount();
        if (rc < 0) {
            rc = 0;
        }
        for (int i = 0; i <= rc; i++) {
            RpcInvoker referer = loadBalance.getInvoker(request.getClassName());
            try {
                return referer.invoke(request);
            } catch (Exception e) {
                if (e instanceof ServiceException) {
                    throw e;
                }
                if (i >= rc) {
                    throw e;
                }
                log.warn(String.format("FailOverHaStrategy Call false for request:%s error=%s", request, e.getMessage()));
            }
        }
        throw new RpcException("FailOverHaStrategy.invoke should not come here!");
    }

}
