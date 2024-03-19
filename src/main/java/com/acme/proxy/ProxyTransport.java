package com.acme.proxy;

import java.util.Optional;

import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.Transport;
import io.helidon.webserver.WebServer;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;

public class ProxyTransport implements Transport {
    @Override
    public boolean isAvailableFor(WebServer webserver) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> createTransportArtifact(Class<T> artifactType,
                                                   String artifactName,
                                                   ServerConfiguration config) {
        if (EventLoopGroup.class.isAssignableFrom(artifactType)) {
            switch (artifactName) {
                case "bossGroup":
                    return Optional.of((T) new NioEventLoopGroup(config.sockets().size()));
                case "workerGroup":
                    return Optional.of((T) new NioEventLoopGroup(Math.max(0, config.workersCount())));
                default:
                    return Optional.empty();
            }
        } else if (ChannelFactory.class.isAssignableFrom(artifactType)) {
            switch (artifactName) {
                case "serverChannelFactory":
                    ChannelFactory<? extends ServerChannel> cf = ProxyServerSocketChannel::new;
                    return Optional.of((T) cf);
                default:
                    return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

}
