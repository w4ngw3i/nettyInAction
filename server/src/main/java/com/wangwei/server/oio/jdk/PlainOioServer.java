package com.wangwei.server.oio.jdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author: wangwei
 * @date: 2019-06-23 10:51
 * 使用jdk内置的api实现阻塞式网络编程
 */
public class PlainOioServer {
    public void server(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port);

        try {
            for (;;){
                Socket clientSocket = socket.accept();

                System.out.println("Accepted connection form " + clientSocket);

                new Thread(() -> {
                    OutputStream out;
                    try {
                        out = clientSocket.getOutputStream();
                        out.write("Hi !\r\n".getBytes(
                                Charset.forName("UTF-8")));
                        out.flush();
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
