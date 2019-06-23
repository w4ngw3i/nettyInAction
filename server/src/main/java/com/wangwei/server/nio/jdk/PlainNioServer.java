package com.wangwei.server.nio.jdk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: wangwei
 * @date: 2019-06-23 13:19
 * 使用jdk内置的api实现异步网络编程
 */
public class PlainNioServer {

    public void server(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        ServerSocket serverSocket = serverSocketChannel.socket();

        InetSocketAddress address = new InetSocketAddress(port);


        /*将服务器绑定到指定的端口*/
        serverSocket.bind(address);

        /*打开selector处理channel*/
        Selector selector = Selector.open();

        /*将serverSocker注册到selector上以接受连接*/
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        final ByteBuffer msg = ByteBuffer.wrap("Hi !\r\n".getBytes());

        for (;;){
            /*等待需要处理的新事件；阻塞将一直持续到下一个传入事件*/
            try {
                selector.select();
            }catch (IOException e){
                e.printStackTrace();
                break;
            }
            /*获取所有接收事件的selectKey实例*/
            Set<SelectionKey> readyKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    /*检查事件是否是一个新的已经就绪可以接受的连接*/
                    if (key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();

                        SocketChannel client = server.accept();

                        client.configureBlocking(false);

                        /*接受客户端，并将它注册到选择器*/
                        client.register(selector, SelectionKey.OP_WRITE |
                                SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection form " + client);
                    }
                    /*检查套节字是否已经准备好写数据*/
                    if (key.isConnectable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();

                        while (buffer.hasRemaining()) {
                            /*将数据写到已连接的客户端*/
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();

                    }
                }catch (IOException e){
                    key.cancel();
                    try {
                        key.channel().close();
                    }catch (IOException cex){

                    }
                }
            }
        }
    }
}
