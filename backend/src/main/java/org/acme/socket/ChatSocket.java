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

@ServerEndpoint("/chat/{conversationId}/{token}")
public class ChatSocket {

    @Inject
    MessageBO messageBO;

    @Inject
    ImageBO imageBO;
    @Inject
    UserBO userBO;

    @Inject
    JWTParser jwtParser;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<Long, Map<Session, Long>> activeSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("conversationId") Long conversationId,
            @PathParam("token") String token) {
        Long userId = getUserIdFromToken(token);
        activeSessions.computeIfAbsent(conversationId, k -> new ConcurrentHashMap<>())
                .put(session, userId);
        System.out.println("WebSocket opened for conversationId " + conversationId);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("conversationId") Long conversationId) {
        Long senderId = activeSessions.get(conversationId).get(session);

        CompletableFuture<String> senderEmailFuture = CompletableFuture.supplyAsync(() -> {
            return userBO.findUserEmailById(senderId);
        });

        // Aguarda até que o email seja obtido e a mensagem seja salva
        senderEmailFuture.thenAccept(senderEmail -> {

            try{
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
                    Message messageSaved = messageBO.sendMessage(conversationId, senderId, senderEmail, currentTimeMillis, text);

                    String imageUrl = imageBO.saveImage(base64Image, conversationId, senderId, messageSaved);

                    // Cria uma URL para a imagem


                    // Envia a mensagem de imagem para o chat

                    // Transmite a mensagem de imagem para todos os clientes conectados
                    broadcastMessage(messageSaved.getId(), conversationId, senderEmail, formattedTime, text,imageUrl);
                } else if (text != null) {
                    // Processa a mensagem de texto
                    Message messageSaved = messageBO.sendMessage(conversationId, senderId, senderEmail, currentTimeMillis, text);

                    // Transmite a mensagem de texto para todos os clientes conectados
                    broadcastMessage(messageSaved.getId(), conversationId, senderEmail, formattedTime, text, null);
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }).exceptionally(ex -> {
            // Trata qualquer exceção que possa ocorrer durante o processo
            ex.printStackTrace();
            return null;
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("conversationId") Long conversationId) {
        Map<Session, Long> sessions = activeSessions.get(conversationId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                activeSessions.remove(conversationId);
            }
        }
        System.out.println("WebSocket closed for conversationId " + conversationId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void broadcastMessage(Long messageId,Long conversationId, String senderEmail, String timeSented, String content, String imageUrl) {
        Map<Session, Long> sessions = activeSessions.get(conversationId);
        if (sessions != null) {
            String formattedMessage = formatMessage(messageId,senderEmail, timeSented, content,imageUrl);
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
        jsonObject.put("imgUrl", imgUrl);
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
