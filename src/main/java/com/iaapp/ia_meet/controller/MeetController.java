package com.iaapp.ia_meet.controller;

import com.iaapp.ia_meet.entity.Meet;
import com.iaapp.ia_meet.service.ChatService;
import com.iaapp.ia_meet.service.MeetService;
import com.iaapp.ia_meet.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/meet")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MeetController {

    private final MeetService meetService;
    private final TokenService tokenService;
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMeet(
            @RequestBody(required = false) Map<String, String> body) {

        String title = body != null
                ? body.getOrDefault("title", "Sans titre")
                : "Sans titre";

        Meet meet = meetService.createMeet(title);

        return ResponseEntity.ok(Map.of(
                "roomId", meet.getRoomId(),
                "title", meet.getTitle(),
                "joinUrl", "/meet/" + meet.getRoomId(),
                "createdAt", meet.getCreatedAt()
        ));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getMeet(@PathVariable String roomId) {

        return meetService.getMeet(roomId)
                .map(meet -> ResponseEntity.ok(Map.of(
                        "roomId", meet.getRoomId(),
                        "title", meet.getTitle(),
                        "active", meet.isActive(),
                        "joinUrl", "/meet/" + meet.getRoomId(),
                        "messages", chatService.getMessages(roomId)
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roomId}/token")
    public ResponseEntity<?> getToken(
            @PathVariable String roomId,
            @RequestParam String username) {

        if (!meetService.meetExists(roomId)) {
            return ResponseEntity.notFound().build();
        }

        String userId = username + "_" +
                UUID.randomUUID().toString().substring(0, 8);

        String token = tokenService.generateToken(roomId, userId);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", userId,
                "roomId", roomId
        ));
    }
}