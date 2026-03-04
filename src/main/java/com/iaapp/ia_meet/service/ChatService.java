package com.iaapp.ia_meet.service;

import com.iaapp.ia_meet.entity.Meet;
import com.iaapp.ia_meet.entity.Message;
import com.iaapp.ia_meet.entity.User;
import com.iaapp.ia_meet.repository.MeetRepository;
import com.iaapp.ia_meet.repository.MessageRepository;
import com.iaapp.ia_meet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final MeetRepository meetRepository;
    private final UserRepository userRepository;

    public Message saveMessage(String roomId, String senderEmail, String content) {
        // Récupérer la réunion par son roomId
        Meet meet = meetRepository.findByRoomId(roomId).orElse(null);
        // Récupérer l'utilisateur par son email (le sender dans le WS est l'email)
        User user = userRepository.findByEmail(senderEmail).orElse(null);

        if (meet != null && user != null) {
            Message message = Message.builder()
                    .meet(meet)
                    .sender(user)
                    .content(content)
                    .build();
            return messageRepository.save(message);
        }
        
        // Si meet ou user n'est pas trouvé (ex: user non connecté qui rejoint), 
        // on ne sauvegarde pas mais le message passera quand même en temps réel via WS
        return null;
    }

    public List<Message> getMessages(String roomId) {
        return messageRepository.findByMeet_RoomIdOrderByTimestampAsc(roomId);
    }
}
