# PPv2 With Helidon 2.x

Implementation of `io.helidon.webserver.Transport` that adds support for Proxy Protocol v2.

## Build and run

With JDK11+
```bash
mvn package
java -jar target/ppv2-h2.jar
```

## Exercise the application

Start a docker container for `haproxy` with the following configuration:

```
frontend main
    bind *:5000
    default_backend             app

backend app
    balance     roundrobin
    server  app1 host.docker.internal:8080 check send-proxy-v2
```

```shell
curl http://localhost:5000
```

```
PeerIdentity{sourceAddress='127.0.0.1', destAddress='127.0.0.1', sourcePort=37344, destPort=5000, vcnMetadata=null, sgwSourceInetAddress='null', sgwPeSourceInetAddress='null', vcnOcid='null', vcnPeOcid='null', authority='null'}
```
