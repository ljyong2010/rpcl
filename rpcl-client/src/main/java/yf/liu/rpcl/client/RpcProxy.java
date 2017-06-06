package yf.liu.rpcl.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yf.liu.rpcl.common.bean.RpcRequest;
import yf.liu.rpcl.common.bean.RpcResponse;
import yf.liu.rpcl.common.util.StringUtil;
import yf.liu.rpcl.registry.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * RPC 代理（用于创建 RPC 服务代理）
 * Created by Administrator on 2017/6/6.
 */
public class RpcProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress){
        this.serviceAddress=serviceAddress;
    }
    public RpcProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass){
        return create(interfaceClass,"");
    }
    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass,final String serviceVersion){
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        //方法定义所在的类
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setServiceVersion(serviceVersion);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceDiscovery !=null){
                            String serviceName = interfaceClass.getName();
                            if (StringUtil.isNotEmpty(serviceVersion)){
                                serviceName += "-" + serviceVersion;
                            }
                            serviceAddress = serviceDiscovery.discover(serviceName);
                            LOGGER.debug("discover service : {} => {}",serviceName,serviceAddress);
                        }
                        if (StringUtil.isEmpty(serviceAddress)){
                            throw new RuntimeException("server address is empty");
                        }
                        String[] array = StringUtil.split(serviceAddress,":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        RpcClient client = new RpcClient(host,port);
                        long time = System.currentTimeMillis();
                        RpcResponse response = client.send(request);
                        LOGGER.debug("time : {}ms",System.currentTimeMillis() - time);
                        if (response == null){
                            throw new RuntimeException("response is null");
                        }
                        if (response.hasException()){
                            throw response.getException();
                        }else
                            return response.getResult();
                    }
                }
        );
    }
}
