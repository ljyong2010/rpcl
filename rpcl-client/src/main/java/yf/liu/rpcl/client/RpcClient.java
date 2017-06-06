package yf.liu.rpcl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yf.liu.rpcl.common.bean.RpcRequest;
import yf.liu.rpcl.common.bean.RpcResponse;
import yf.liu.rpcl.common.codec.RpcDecoder;
import yf.liu.rpcl.common.codec.RpcEncoder;

/**
 * Created by Administrator on 2017/6/6.
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private final String host;
    private final int port;

    private RpcResponse response;
    public RpcClient(String host,int port){
        this.host = host;
        this.port = port;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.response = rpcResponse;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        LOGGER.error("api caught exception",cause);
        ctx.close();
    }
    public RpcResponse send(RpcRequest request) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));
                    pipeline.addLast(RpcClient.this);
                }
            });
            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            ChannelFuture future = bootstrap.connect(host,port).sync();

            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return response;
        }finally {
            group.shutdownGracefully();
        }
    }
}
