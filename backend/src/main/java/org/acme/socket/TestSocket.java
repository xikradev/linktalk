package org.acme.socket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint("/chat/test")
public class TestSocket {
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connected");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText("Received: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }
}
