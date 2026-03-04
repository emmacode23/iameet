package com.iaapp.ia_meet.repository;

import com.iaapp.ia_meet.entity.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetRepository extends JpaRepository<Meet, String> {
    Optional<Meet> findByRoomId(String roomId);
}
