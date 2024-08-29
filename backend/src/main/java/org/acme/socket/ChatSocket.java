package org.acme.socket;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Produces;
import org.acme.model.bo.ChatBO;
import org.eclipse.microprofile.jwt.JsonWebToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChatSocket {

    @Inject
    ChatBO chatBO;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, Map<String, ServerWebSocket>> activeSessions = new ConcurrentHashMap<>();

    @Inject
    Vertx vertx;
    @Inject
    JWTParser jwtParser;

    @PostConstruct
    void setup() {
        HttpServer server = vertx.createHttpServer();
        server.webSocketHandler(this::handleWebSocket);
        server.listen(8080);
    }

    private void handleWebSocket(ServerWebSocket ws) {
        String path = ws.path();
        if (!path.startsWith("/chat/")) {
            ws.reject(); // Rejeita se a URL não começar com "/chat/"
            return;
        }

        String conversationIdStr = path.substring("/chat/".length());
        Long conversationId;
        try {
            conversationId = Long.parseLong(conversationIdStr);
        } catch (NumberFormatException e) {
            ws.reject(); // Rejeita se o conversationId não for um número válido
            return;
        }

        activeSessions.computeIfAbsent(conversationId, k -> new ConcurrentHashMap<>())
                .put(ws.textHandlerID(), ws);

        ws.textMessageHandler(message -> {
            Long senderId = getUserIdFromWebSocket(ws);
            chatBO.sendMessage(conversationId, senderId, message);
            broadcastMessage(conversationId, senderId, message);
        });

        ws.closeHandler(v -> {
            Map<String, ServerWebSocket> sessions = activeSessions.get(conversationId);
            if (sessions != null) {
                sessions.remove(ws.textHandlerID());
                if (sessions.isEmpty()) {
                    activeSessions.remove(conversationId);
                }
            }
        });

        ws.exceptionHandler(Throwable::printStackTrace);
    }

    private void broadcastMessage(Long conversationId, Long senderId, String content) {
        Map<String, ServerWebSocket> sessions = activeSessions.get(conversationId);
        if (sessions != null) {
            String formattedMessage = formatMessage(senderId, content);
            sessions.values().forEach(session -> session.writeTextMessage(formattedMessage));
        }
    }
    public String formatMessage(Long senderId, String content) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("senderId", senderId);
        jsonObject.put("content", content);
        return jsonObject.toString();
    }


    private Long getUserIdFromWebSocket(ServerWebSocket ws) {
        String token = extractTokenFromQuery(ws.query());
        if (token != null) {
            try {
                JsonWebToken jwt = jwtParser.parse(token);
                // Supondo que o ID do usuário esteja em um claim chamado "userId"
                Optional<Long> userId = jwt.getClaim("userId");
                return userId.orElseThrow(() -> new RuntimeException("Invalid JWT: missing userId claim"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to parse JWT", e);
            }
        } else {
            throw new RuntimeException("Missing or invalid token");
        }
    }

    private String extractTokenFromQuery(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        // Divida os parâmetros de consulta
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }
}
