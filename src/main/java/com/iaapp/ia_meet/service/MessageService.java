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
public class MessageService {

    private final MessageRepository messageRepository;
    private final MeetRepository meetRepository;
    private final UserRepository userRepository;

    public Message saveMessage(String roomId, String senderEmail, String content) {
        Meet meet = meetRepository.findByRoomId(roomId).orElse(null);
        User user = userRepository.findByEmail(senderEmail).orElse(null);

        if (meet != null && user != null) {
            Message message = Message.builder()
                    .meet(meet)
                    .sender(user)
                    .content(content)
                    .build();
            return messageRepository.save(message);
        }
        return null;
    }

    public List<Message> getMessagesByMeet(String roomId) {
        return messageRepository.findByMeet_RoomIdOrderByTimestampAsc(roomId);
    }
}