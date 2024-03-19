package com.acme.proxy;

import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutorGroup;

class DelegatingChannelPipeline implements ChannelPipeline {
    private final ChannelPipeline delegate;

    DelegatingChannelPipeline(ChannelPipeline delegate) {
        this.delegate = delegate;
    }

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return delegate.addFirst(name, handler);
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
        return delegate.addFirst(group, name, handler);
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        return delegate.addLast(name, handler);
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        return delegate.addLast(group, name, handler);
    }

    @Override
    public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        return delegate.addBefore(baseName, name, handler);
    }

    @Override
    public ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        return delegate.addBefore(group, baseName, name, handler);
    }

    @Override
    public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        return delegate.addAfter(baseName, name, handler);
    }

    @Override
    public ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        return delegate.addAfter(group, baseName, name, handler);
    }

    @Override
    public ChannelPipeline addFirst(ChannelHandler... handlers) {
        return delegate.addFirst(handlers);
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup group, ChannelHandler... handlers) {
        return delegate.addFirst(group, handlers);
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler... handlers) {
        return delegate.addLast(handlers);
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup group, ChannelHandler... handlers) {
        return delegate.addLast(group, handlers);
    }

    @Override
    public ChannelPipeline remove(ChannelHandler handler) {
        return delegate.remove(handler);
    }

    @Override
    public ChannelHandler remove(String name) {
        return delegate.remove(name);
    }

    @Override
    public <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return delegate.remove(handlerType);
    }

    @Override
    public ChannelHandler removeFirst() {
        return delegate.removeFirst();
    }

    @Override
    public ChannelHandler removeLast() {
        return delegate.removeLast();
    }

    @Override
    public ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        return delegate.replace(oldHandler, newName, newHandler);
    }

    @Override
    public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return delegate.replace(oldName, newName, newHandler);
    }

    @Override
    public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return delegate.replace(oldHandlerType, newName, newHandler);
    }

    @Override
    public ChannelHandler first() {
        return delegate.first();
    }

    @Override
    public ChannelHandlerContext firstContext() {
        return delegate.firstContext();
    }

    @Override
    public ChannelHandler last() {
        return delegate.last();
    }

    @Override
    public ChannelHandlerContext lastContext() {
        return delegate.lastContext();
    }

    @Override
    public ChannelHandler get(String name) {
        return delegate.get(name);
    }

    @Override
    public <T extends ChannelHandler> T get(Class<T> handlerType) {
        return delegate.get(handlerType);
    }

    @Override
    public ChannelHandlerContext context(ChannelHandler handler) {
        return delegate.context(handler);
    }

    @Override
    public ChannelHandlerContext context(String name) {
        return delegate.context(name);
    }

    @Override
    public ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
        return delegate.context(handlerType);
    }

    @Override
    public Channel channel() {
        return delegate.channel();
    }

    @Override
    public List<String> names() {
        return delegate.names();
    }

    @Override
    public Map<String, ChannelHandler> toMap() {
        return delegate.toMap();
    }

    @Override
    public ChannelPipeline fireChannelRegistered() {
        return delegate.fireChannelRegistered();
    }

    @Override
    public ChannelPipeline fireChannelUnregistered() {
        return delegate.fireChannelUnregistered();
    }

    @Override
    public ChannelPipeline fireChannelActive() {
        return delegate.fireChannelActive();
    }

    @Override
    public ChannelPipeline fireChannelInactive() {
        return delegate.fireChannelInactive();
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable cause) {
        return delegate.fireExceptionCaught(cause);
    }

    @Override
    public ChannelPipeline fireUserEventTriggered(Object event) {
        return delegate.fireUserEventTriggered(event);
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        return delegate.fireChannelRead(msg);
    }

    @Override
    public ChannelPipeline fireChannelReadComplete() {
        return delegate.fireChannelReadComplete();
    }

    @Override
    public ChannelPipeline fireChannelWritabilityChanged() {
        return delegate.fireChannelWritabilityChanged();
    }

    @Override
    public ChannelPipeline flush() {
        return delegate.flush();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return delegate.bind(localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return delegate.connect(remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return delegate.connect(remoteAddress, localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return delegate.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return delegate.close();
    }

    @Override
    public ChannelFuture deregister() {
        return delegate.deregister();
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return delegate.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return delegate.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return delegate.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return delegate.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return delegate.close(promise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return delegate.deregister(promise);
    }

    @Override
    public ChannelOutboundInvoker read() {
        return delegate.read();
    }

    @Override
    public ChannelFuture write(Object msg) {
        return delegate.write(msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return delegate.write(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return delegate.writeAndFlush(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return delegate.writeAndFlush(msg);
    }

    @Override
    public ChannelPromise newPromise() {
        return delegate.newPromise();
    }

    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return delegate.newProgressivePromise();
    }

    @Override
    public ChannelFuture newSucceededFuture() {
        return delegate.newSucceededFuture();
    }

    @Override
    public ChannelFuture newFailedFuture(Throwable cause) {
        return delegate.newFailedFuture(cause);
    }

    @Override
    public ChannelPromise voidPromise() {
        return delegate.voidPromise();
    }

    @Override
    public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return delegate.iterator();
    }

    @Override
    public void forEach(Consumer<? super Map.Entry<String, ChannelHandler>> action) {
        delegate.forEach(action);
    }

    @Override
    public Spliterator<Map.Entry<String, ChannelHandler>> spliterator() {
        return delegate.spliterator();
    }
}
