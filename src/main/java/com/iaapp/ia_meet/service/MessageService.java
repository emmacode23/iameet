package com.iaapp.ia_meet.service;

import com.iaapp.ia_meet.entity.Message;
import com.iaapp.ia_meet.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message saveMessage(String roomId, String sender, String content) {
        Message message = Message.builder()
                .roomId(roomId)
                .sender(sender)
                .content(content)
                .build();
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByMeet(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}
