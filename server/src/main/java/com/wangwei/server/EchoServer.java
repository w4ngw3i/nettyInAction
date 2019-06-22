package com.wangwei.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author wangwei
 * @datatime 2019-06-16 17:25
 */
public class EchoServer {

//    private final int port;
    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 1) {
//            System.out.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
//            return;
//        }

//        int port = Integer.parseInt(args[0]);


        new EchoServer(8890).start();

    }

    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();

        EventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(serverHandler);
                    }
                });

        /**
         * 异步绑定服务器，调用sync方法阻塞，等待直到绑定完成
         */
        ChannelFuture f = bootstrap.bind().sync();

        /**
         * 获取channel的closeFuture并且阻塞当前线程直到它完成
         */
        f.channel().closeFuture().sync();

        /**
         * 关闭EventLoopGroup释放所有资源
         */
        group.shutdownGracefully().sync();


    }

}
