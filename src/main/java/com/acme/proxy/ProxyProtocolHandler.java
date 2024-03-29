package com.acme.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.helidon.common.context.Context;
import io.helidon.common.context.Contexts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.haproxy.HAProxyCommand;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyProxiedProtocol;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import io.netty.util.AttributeKey;

public class ProxyProtocolHandler extends SimpleChannelInboundHandler<HAProxyMessage> {
    private static final Logger LOGGER = Logger.getLogger(ProxyProtocolHandler.class.getName());
    static final AttributeKey<PeerIdentity> PROXY_PEER_IDENTITY = AttributeKey.valueOf("proxy_peer_identity");

    /**
     * A decoder that throws decoding exceptions on byte arrays that do not contain ASCII.
     */
    private final CharsetDecoder asciiDecoder = StandardCharsets.US_ASCII
            .newDecoder()
            .onUnmappableCharacter(CodingErrorAction.REPORT)
            .onMalformedInput(CodingErrorAction.REPORT);

    /**
     * Some invalid inputs may cause {@link io.netty.handler.codec.haproxy.HAProxyMessageDecoder} to throw multiple
     * exceptions during decoding. This state variable ensures that failure metrics are only incremented once.
     */
    private boolean alreadyFailed = false;

    public ProxyProtocolHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage haProxyMessage) {
        HAProxyCommand command = haProxyMessage.command();
        if (command != HAProxyCommand.PROXY) {
            throw new ProxyProtocolException("Invalid command: " + command);
        }

        HAProxyProxiedProtocol proto = haProxyMessage.proxiedProtocol();
        if (proto != HAProxyProxiedProtocol.TCP4 && proto != HAProxyProxiedProtocol.TCP6) {
            throw new ProxyProtocolException("Invalid proxied protocol: " + proto);
        }

        PeerIdentity.Builder peerIdentityBuilder = PeerIdentity.builder()
                .sourceAddress(haProxyMessage.sourceAddress())
                .destAddress(haProxyMessage.destinationAddress())
                .sourcePort(haProxyMessage.sourcePort())
                .destPort(haProxyMessage.destinationPort());

        for (HAProxyTLV tlv : haProxyMessage.tlvs()) {
            if (tlv.type() != HAProxyTLV.Type.OTHER) {
                continue;
            }

            byte literalType = tlv.typeByteValue();
            OciType ociType = OciType.typeForByteValue(literalType);
            // WARNING - Assumes HAProxyMessageDecoder was given a suitably low TLV length limit.
            ByteBuf content = tlv.content();

            switch (ociType) {
                case VCN_CUSTOM -> peerIdentityBuilder.vcnMetadata(ByteBufUtil.getBytes(content));
                case SGWIP_CUSTOM -> peerIdentityBuilder.sgwSourceInetAddress(inetAddress(ociType, content));
                case SGWPEIP_CUSTOM -> peerIdentityBuilder.sgwPeSourceInetAddress(inetAddress(ociType, content));
                case VCN_OCID_CUSTOM -> peerIdentityBuilder.vcnOcid(asciiString(ociType, content));
                case SGWPE_OCID_CUSTOM -> peerIdentityBuilder.vcnPeOcid(asciiString(ociType, content));
                case AUTHORITY -> peerIdentityBuilder.authority(asciiString(ociType, content));
                default -> LOGGER.info("Unknown TLV type " + ociType);
            }
        }
        // Set peer identity as channel attribute
        PeerIdentity peerIdentity = peerIdentityBuilder.build();
        ctx.channel().attr(PROXY_PEER_IDENTITY).set(peerIdentity);
        Context context = Contexts.context().orElseGet(Contexts::globalContext);
        context.register(peerIdentity);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Could not parse proxy protocol v2 header", cause);
        if (!alreadyFailed) {
            ctx.close();
            alreadyFailed = true;
        }
    }

    /**
     * Validate that a {@link io.netty.buffer.ByteBuf} contains a valid IP address (IPv4 or IPv6), and return that address as a
     * string suitable for use in logging.
     *
     * @param type the type of metadata this IP was meant to convey; used in error reporting
     * @param content the IP as bytes
     * @return the parsed IP as a string
     */
    private String inetAddress(OciType type, ByteBuf content) {
        byte[] address = ByteBufUtil.getBytes(content);
        try {
            return InetAddress.getByAddress(address).getHostAddress();
        } catch (UnknownHostException e) {
            throw new ProxyProtocolException("could not parse IP address for " + type, e);
        }
    }

    /**
     * Extract an ASCII-encoded string from a {@link io.netty.buffer.ByteBuf}.
     *
     * @param type the type of metadata this IP was meant to convey; used in error reporting
     * @param content the ASCII data
     * @return the decoded string
     */
    private String asciiString(OciType type, ByteBuf content) {
        return asciiString(type, content.nioBuffer());
    }

    /**
     * Extract an ASCII-encoded string from a {@link java.nio.ByteBuffer}.
     *
     * @param type the type of metadata this IP was meant to convey; used in error reporting
     * @param buffer the ASCII data as an NIO byte buffer
     * @return the decoded string
     */
    private String asciiString(OciType type, ByteBuffer buffer) {
        try {
            return asciiDecoder.decode(buffer).toString();
        } catch (CharacterCodingException e) {
            throw new ProxyProtocolException("failed to decode " + type, e);
        }
    }

    public enum OciType {
        /**
         * The raw VCN metadata.  Useful for debugging
         */
        VCN_CUSTOM,
        /**
         * The Class E address inserted into source address by Service Gateway
         */
        SGWIP_CUSTOM,
        /**
         * The Class E address inserted into source address by Prive Endpoints
         */
        SGWPEIP_CUSTOM,
        /**
         * The OCID that identifies the originating VCN
         */
        VCN_OCID_CUSTOM,
        /**
         * The OCID that identifies the originating private endpoint
         */
        SGWPE_OCID_CUSTOM,
        /**
         * The client's Server Name Indication field, if the load balancer performed TLS termination.
         * Not used by KMS
         */
        AUTHORITY,
        /**
         * A Proxy Protocol V2 TLV type unknown to both Netty and this handler.
         */
        UNKNOWN;

        public static OciType typeForByteValue(byte byteValue) {
            return switch (byteValue) {
                case (byte) 0xE0 -> VCN_CUSTOM;
                case (byte) 0xE1 -> SGWIP_CUSTOM;
                case (byte) 0xE2 -> SGWPEIP_CUSTOM;
                case (byte) 0xE3 -> VCN_OCID_CUSTOM;
                case (byte) 0xE4 -> SGWPE_OCID_CUSTOM;
                case (byte) 0x02 -> AUTHORITY;
                default -> UNKNOWN;
            };
        }

        static byte byteValueForType(OciType type) {
            return switch (type) {
                case VCN_CUSTOM -> (byte) 0xE0;
                case SGWIP_CUSTOM -> (byte) 0xE1;
                case SGWPEIP_CUSTOM -> (byte) 0xE2;
                case VCN_OCID_CUSTOM -> (byte) 0xE3;
                case SGWPE_OCID_CUSTOM -> (byte) 0xE4;
                case AUTHORITY -> (byte) 0x02;
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
        }
    }
}
