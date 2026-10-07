package org.acme;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

// Not annotated: registered by EchoApplicationConfig
public class ProgrammaticEchoEndpoint extends Endpoint {

    public static final String PATH = "/programmatic-echo";

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(String.class,
                (MessageHandler.Whole<String>) message -> session
                        .getAsyncRemote().sendText("echo: " + message));
        session.getAsyncRemote().sendText("welcome");
    }
}
