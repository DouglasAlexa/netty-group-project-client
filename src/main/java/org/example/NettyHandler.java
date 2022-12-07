package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.time.LocalDate;

public class NettyHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        var packageId = byteBuf.readByte();

        if (packageId == 0) {
            // receive message from server
        } else if (packageId == 1) {
            // receive url from server
        } else if (packageId == 2) {
            // receive date from server
            System.out.println("Server said date is: " + readDate(byteBuf));
        }
    }

    private static LocalDate readDate(ByteBuf byteBuf) {
        var length = byteBuf.readInt();
        var buffer = new byte[length];
        byteBuf.readBytes(buffer, 0, length);
        String date = new String(buffer, 0, length);
        System.out.println(date);
        System.out.println(Integer.parseInt(date.substring(8, 9)));
        try {
            LocalDate dateFromServer = LocalDate.of(Integer.parseInt(date.substring(0, 3)), Integer.parseInt(date.substring(5, 6)), Integer.parseInt(date.substring(8, 9)));
            return dateFromServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
