package com.example.sulmuro_app.repository.chat;

import com.example.sulmuro_app.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}