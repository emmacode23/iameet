package com.iaapp.ia_meet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "meets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meet {

    @Id
    @Column(unique = true, nullable = false)
    private String roomId; // UUID generated

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean active;
    
    // Optional: Title of the meeting
    private String title;

    // We can store participant count or list if needed later
}
