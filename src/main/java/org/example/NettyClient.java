package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class NettyClient {

    private final String address;
    private final int port;
    private final EventLoopGroup eventLoops;

    public NettyClient(String address, int port) {
        this.address = address;
        this.port = port;
        this.eventLoops = new NioEventLoopGroup();
    }

    public void start() {
        try {
            Bootstrap bootstrap = new Bootstrap();

            var channel = bootstrap
                    .group(eventLoops)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addFirst(new NettyHandler());
                        }
                    })
                    .connect(address, port).sync().channel();

            // send to server
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            while (!line.equals("$exit")) {

                if (line.equals("$send")) {
                    // request server to print message
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeByte(0);
                    byteBuf.writeInt(line.length());
                    byteBuf.writeBytes(line.getBytes());

                    channel.writeAndFlush(byteBuf);
                } else if (line.equals("$url")) {
                    // request server to open browser with this url
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeByte(1);
                    byteBuf.writeInt(line.length());
                    byteBuf.writeBytes(line.getBytes());

                    channel.writeAndFlush(byteBuf);
                } else if (line.equals("$date")) {
                    // ask server for date
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeByte(2);

                    channel.writeAndFlush(byteBuf);
                }

                line = scanner.nextLine();
            }

            eventLoops.shutdownGracefully();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
