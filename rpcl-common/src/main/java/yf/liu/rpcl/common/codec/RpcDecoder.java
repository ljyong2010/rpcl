package yf.liu.rpcl.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import yf.liu.rpcl.common.util.SerializationUtil;

import java.util.List;

/**
 * RPC 解码器
 * Created by Administrator on 2017/6/5.
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private Class<?> genericClass;
    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4){
            return;
        }
        in.markReaderIndex();

        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(SerializationUtil.deserialize(data,genericClass));
    }
}
