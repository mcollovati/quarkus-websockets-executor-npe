package org.acme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class EchoEndpointTest {

    @TestHTTPResource("/echo")
    URI uri;

    @Test
    void annotatedEndpointCallbacksRun() throws Exception {
        Client client = new Client();
        try (Session session = ContainerProvider.getWebSocketContainer()
                .connectToServer(client, uri)) {
            assertEquals("CONNECT", client.next());
            // Sent from the server's @OnOpen
            assertEquals("welcome", client.next());
            session.getBasicRemote().sendText("hello");
            // Sent from the server's @OnMessage
            assertEquals("echo: hello", client.next());
        }
    }

    @ClientEndpoint
    public static class Client {
        final LinkedBlockingDeque<String> messages = new LinkedBlockingDeque<>();

        @OnOpen
        public void open(Session session) {
            messages.add("CONNECT");
        }

        @OnMessage
        void message(String msg) {
            messages.add(msg);
        }

        String next() throws InterruptedException {
            return messages.poll(10, TimeUnit.SECONDS);
        }
    }
}
