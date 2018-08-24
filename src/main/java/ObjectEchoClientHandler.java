/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import messaging.MsgEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Handler implementation for the object echo client.  It initiates the
 * ping-pong traffic between the object echo client and server by sending the
 * first message to the server.
 */
public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter {

    private final MsgEvent me;
    private final String id;
    private Timer timer;
    private long start_ts;
    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler(Timer timer, MsgEvent me) {
        this.start_ts = System.currentTimeMillis();
        this.timer = timer;
        this.id = UUID.randomUUID().toString();
        this.me = me;
        this.me.setParam("key",id);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
        ctx.writeAndFlush(me);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the server.
        //ctx.write(msg);
        //ctx.close();
        MsgEvent rme = (MsgEvent)msg;
        if(rme.getParam("key").equals(id)) {
            timer.record(System.currentTimeMillis() - start_ts, TimeUnit.MILLISECONDS);
        }

        //System.out.println(rme.getParams());

        //System.out.println("GOT MESSAGE :" + Thread.currentThread().getId());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        //ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        System.out.println("whut");
    }
}
