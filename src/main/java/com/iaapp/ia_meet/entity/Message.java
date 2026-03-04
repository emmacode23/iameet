package com.iaapp.ia_meet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation vers la réunion
    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "roomId")
    private Meet meet;

    // Relation vers l'utilisateur qui envoie le message
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime timestamp;
}