package com.example.sulmuro_app.repository.chat;

import com.example.sulmuro_app.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}