package com.iaapp.ia_meet.service;

import com.iaapp.ia_meet.entity.Meet;
import com.iaapp.ia_meet.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;

    public Meet createMeet(String title) {
        String roomId = UUID.randomUUID().toString();
        Meet meet = Meet.builder()
                .roomId(roomId)
                .title(title)
                .active(true)
                .build();
        return meetRepository.save(meet);
    }

    public Optional<Meet> getMeet(String roomId) {
        return meetRepository.findByRoomId(roomId);
    }

    public boolean meetExists(String roomId) {
        return meetRepository.existsById(roomId);
    }
}
