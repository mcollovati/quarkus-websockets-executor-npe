package org.acme;

import java.util.Set;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;

public class EchoApplicationConfig implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> endpointClasses) {
        return Set.of(ServerEndpointConfig.Builder
                .create(ProgrammaticEchoEndpoint.class,
                        ProgrammaticEchoEndpoint.PATH)
                .build());
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Keep EchoEndpoint
        return scanned;
    }
}
