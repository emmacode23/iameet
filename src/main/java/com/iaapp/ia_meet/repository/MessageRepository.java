package com.iaapp.ia_meet.repository;

import com.iaapp.ia_meet.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Cette méthode permet de trouver les messages par le roomId de l'entité Meet associée
    List<Message> findByMeet_RoomIdOrderByTimestampAsc(String roomId);
}