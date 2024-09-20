package org.acme.socket;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.acme.model.bo.ImageBO;
import org.acme.model.bo.MessageBO;
import org.acme.model.bo.UserBO;
import org.acme.model.entity.Message;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.smallrye.jwt.auth.principal.JWTParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ServerEndpoint("/group/{groupId}/{token}")
public class GroupSocket {

    @Inject
    MessageBO messageBO;

    @Inject
    ImageBO imageBO;

    @Inject
    UserBO userBO;

    @Inject
    JWTParser jwtParser;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<Long, Map<Session, Long>> activeGroupSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("groupId") Long groupId,
                       @PathParam("token") String token) {
        Long userId = getUserIdFromToken(token);
        // Aqui você pode adicionar lógica para verificar se o usuário faz parte do grupo
        activeGroupSessions.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>())
                .put(session, userId);
        System.out.println("WebSocket opened for groupId " + groupId);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("groupId") Long groupId) {
        Long senderId = activeGroupSessions.get(groupId).get(session);

        CompletableFuture<String> senderEmailFuture = CompletableFuture.supplyAsync(() -> {
            return userBO.findUserEmailById(senderId);
        });

        senderEmailFuture.thenAccept(senderEmail -> {

            try {
                JsonObject jsonMessage = Json.createReader(new StringReader(message)).readObject();
                String text = jsonMessage.getString("text", null);
                String base64Image = jsonMessage.getString("image", null);

                Long currentTimeMillis = System.currentTimeMillis();
                LocalTime time = Instant.ofEpochMilli(currentTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String formattedTime = time.format(formatter);

                if (base64Image != null) {
                    // Processa a imagem
                    Message messageSaved = messageBO.sendMessageToGroup(groupId, senderId, senderEmail, currentTimeMillis, text);

                    String imageUrl = imageBO.saveImage(base64Image, groupId, senderId, messageSaved, "group");

                    // Transmite a mensagem de imagem para todos os clientes conectados
                    broadcastMessage(messageSaved.getId(), groupId, senderEmail, formattedTime, text, imageUrl);
                } else if (text != null) {
                    // Processa a mensagem de texto
                    Message messageSaved = messageBO.sendMessageToGroup(groupId, senderId, senderEmail, currentTimeMillis, text);

                    // Transmite a mensagem de texto para todos os clientes conectados
                    broadcastMessage(messageSaved.getId(), groupId, senderEmail, formattedTime, text, null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("groupId") Long groupId) {
        Map<Session, Long> sessions = activeGroupSessions.get(groupId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                activeGroupSessions.remove(groupId);
            }
        }
        System.out.println("WebSocket closed for groupId " + groupId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void broadcastMessage(Long messageId, Long groupId, String senderEmail, String timeSented, String content, String imageUrl) {
        Map<Session, Long> sessions = activeGroupSessions.get(groupId);
        if (sessions != null) {
            String formattedMessage = formatMessage(messageId, senderEmail, timeSented, content, imageUrl);
            sessions.keySet().forEach(session -> {
                CompletableFuture.runAsync(() -> {
                    try {
                        session.getBasicRemote().sendText(formattedMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    private String formatMessage(Long messageId, String senderEmail, String timeSented, String content, String imgUrl) {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("id", messageId);
        jsonObject.put("senderEmail", senderEmail);
        jsonObject.put("content", content);
        if(imgUrl != null){
            jsonObject.put("imgUrl", imgUrl);
        }
        jsonObject.put("timeSented", timeSented);
        return jsonObject.toString();
    }

    private Long getUserIdFromToken(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            Object userIdClaim = jwt.getClaim("userId");
            Long userId = Long.parseLong(userIdClaim.toString());
            return userId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JWT", e);
        }
    }
}

