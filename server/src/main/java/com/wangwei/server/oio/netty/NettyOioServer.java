package com.wangwei.server.oio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author: wangwei
 * @date: 2019-06-23 14:46
 */
public class NettyOioServer {
    public void server(int port) throws InterruptedException {
       final ByteBuf buf = Unpooled.unreleasableBuffer(
                Unpooled.copiedBuffer("Hi !\r\n", Charset.forName("UTF-8"))
        );

        EventLoopGroup group = new OioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    /*允许阻塞模式*/
                    .channel(OioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    /*指定ChannelInitializer，对于每个已接受的连接都调用它*/
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    /*添加一个ChannelInboundHandlerAdapter以拦截和处理事件*/
                                    new ChannelInboundHandlerAdapter(){
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            /*将消息写到客户端并添加ChannelFutureListener，以便消息一被写完就关闭连接*/
                                            ctx.writeAndFlush(buf.duplicate())
                                                    .addListener(ChannelFutureListener.CLOSE);
                                        }
                                    });
                        }
                    });
            /*绑定服务器以接受连接*/
            ChannelFuture f = serverBootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            /*释放所有的资源*/
            group.shutdownGracefully().sync();
        }
    }
}
