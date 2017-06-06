package yf.liu.rpcl.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import yf.liu.rpcl.common.util.SerializationUtil;

/**
 * RPC 编码器
 * Created by Administrator on 2017/6/5.
 */
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;
    public RpcEncoder(Class<?> genericClass){
        this.genericClass=genericClass;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)){
            byte[] data = SerializationUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
