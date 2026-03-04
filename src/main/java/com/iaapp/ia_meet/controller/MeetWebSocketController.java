
package com.iaapp.ia_meet.controller;

import com.iaapp.ia_meet.service.ChatService;
import com.iaapp.ia_meet.websocket.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MeetWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketMessage message) {
        // Enregistrer le message en BDD pour l'historique
        chatService.saveMessage(message.getRoomId(), message.getSender(), message.getContent());
        
        message.setType(WebSocketMessage.MessageType.CHAT);
        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload WebSocketMessage message, SimpMessageHeaderAccessor headerAccessor) {
        // Ajouter username dans la session WebSocket
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());

        message.setType(WebSocketMessage.MessageType.JOIN);
        message.setContent(message.getSender() + " a rejoint la réunion.");
        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
    }

    @MessageMapping("/chat.peer")
    public void sendPeerId(@Payload WebSocketMessage message) {
        // Diffuser l'ID PeerJS aux autres participants
        message.setType(WebSocketMessage.MessageType.PEER_ID);
        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
    }
}
