package com.wangwei.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author wangwei
 * @datatime 2019-06-16 18:23
 */
public class EchoClient {
    private static String host;

    static {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            host = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
//        if (args.length != 2) {
//            System.out.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
//            return;
//        }
//
//        String host = args[0];
//        int port = Integer.parseInt(args[1]);
        new EchoClient(host, 8890).start();
    }


    public void start() throws InterruptedException {
      EventLoopGroup group = new NioEventLoopGroup();
      try {
          Bootstrap bootstrap = new Bootstrap();
          bootstrap.group(group)
                  .channel(NioSocketChannel.class)
                  /*设置服务器的地址*/
                  .remoteAddress(new InetSocketAddress(host, port))
                  .handler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ClientChannelHandler());
                      }
                  });
          /*连接到远程节点，阻塞等待直到连接完成*/
          ChannelFuture f = bootstrap.connect().sync();
          /*阻塞，直到channel关闭*/
          f.channel().closeFuture().sync();
      }finally {
          group.shutdownGracefully().sync();
      }
    }
}
