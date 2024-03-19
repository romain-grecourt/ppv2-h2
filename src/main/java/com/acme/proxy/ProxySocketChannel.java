package com.acme.proxy;

import java.nio.channels.SocketChannel;

import io.helidon.common.LazyValue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;

final class ProxySocketChannel extends NioSocketChannel {

    final LazyValue<ChannelPipeline> pipeline = LazyValue.create(this::newPipeline);

    ProxySocketChannel(Channel parent, SocketChannel ch) {
        super(parent, ch);
    }

    private ChannelPipeline newPipeline() {
        return new ProxyChannelPipeline(super.pipeline());
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline.get();
    }
}
