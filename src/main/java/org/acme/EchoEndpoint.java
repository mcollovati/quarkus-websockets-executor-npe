package org.acme;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/echo")
public class EchoEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        session.getAsyncRemote().sendText("welcome");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        session.getAsyncRemote().sendText("echo: " + message);
    }
}
