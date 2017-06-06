package yf.liu.rpcl.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yf.liu.rpcl.registry.ServiceRegistry;

/**
 * 基于 ZooKeeper 的服务注册接口实现
 * Created by Administrator on 2017/6/5.
 */
public class ZooKeeperServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceRegistry.class);

    private final ZkClient zkClient;
    public ZooKeeperServiceRegistry(String zkAddress){
        zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)){
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node: {}",registryPath);
        }
        String servicePath = registryPath +"/"+serviceName;
        if (!zkClient.exists(servicePath)){
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node: {}",servicePath);
        }
        String addressPath = servicePath + "/" + "address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath,serviceAddress);
        LOGGER.debug("create address node: {}",addressNode);
    }
}
