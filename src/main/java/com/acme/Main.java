package com.acme;

import io.helidon.common.reactive.Single;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;

import com.acme.proxy.PeerIdentity;
import com.acme.proxy.ProxyTransport;

public final class Main {

    private Main() {
    }

    public static void main(final String[] args) {
        startServer();
    }

    static Single<WebServer> startServer() {
        WebServer server = WebServer.builder(createRouting())
                .port(8080)
                .transport(new ProxyTransport()) // register proxy transport
                .build();

        Single<WebServer> webserver = server.start();

        webserver.thenAccept(ws -> {
                    System.out.println("WEB server is up! http://localhost:" + ws.port() + "/");
                    ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
                })
                .exceptionallyAccept(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    t.printStackTrace(System.err);
                });

        return webserver;
    }

    private static Routing createRouting() {
        return Routing.builder()
                .get("/", (req, res) -> {
                    // get the peer identity from the request context
                    PeerIdentity peerIdentity = req.context().get(PeerIdentity.class).orElseThrow();
                    res.send(peerIdentity.toString());
                })
                .build();
    }
}
