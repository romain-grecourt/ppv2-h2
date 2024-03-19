package com.acme.proxy;

class ProxyProtocolException extends RuntimeException {

    public ProxyProtocolException(String message) {
        super(message);
    }

    public ProxyProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
