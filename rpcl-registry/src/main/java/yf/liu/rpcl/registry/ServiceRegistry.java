package yf.liu.rpcl.registry;

/**
 * 服务注册接口
 * Created by Administrator on 2017/6/5.
 */
public interface ServiceRegistry {

    /**
     * 注册服务名称与服务地址
     * @param serviceName 服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName,String serviceAddress);
}
