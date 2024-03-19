package com.acme.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.ssl.SslHandler;

final class ProxyChannelPipeline extends DelegatingChannelPipeline {

    private final HAProxyMessageDecoder haProxyHandler = new HAProxyMessageDecoder();
    private final ProxyProtocolHandler proxyHandler = new ProxyProtocolHandler();
    private boolean added;

    ProxyChannelPipeline(ChannelPipeline delegate) {
        super(delegate);
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... handlers) {
        if (!added) {
            if (handlers.length > 0) {
                ChannelHandler handler = handlers[0];
                if (handlers[0].getClass().getName().equals("io.helidon.webserver.HttpInitializer")) {
                    return super.addLast(handlers);
                }
                added = true;
                if (handler instanceof SslHandler) {
                    super.addLast(handler, haProxyHandler, proxyHandler);
                } else {
                    super.addLast(haProxyHandler, proxyHandler, handler);
                }
                return this;
            }
        }
        super.addLast(handlers);
        return this;
    }
}
