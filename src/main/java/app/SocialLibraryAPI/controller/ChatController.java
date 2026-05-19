package app.SocialLibraryAPI.controller;

import app.SocialLibraryAPI.dto.request.SendChatMessageRequest;
import app.SocialLibraryAPI.dto.response.ChatMessageDTO;
import app.SocialLibraryAPI.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{sessionId}/sendMessage")
    public void sendMessage(
            @DestinationVariable Integer sessionId,
            @Payload SendChatMessageRequest request,
            Principal principal) {

        log.info("Received WS message in session {} from user {}", sessionId, principal.getName());

        ChatMessageDTO savedMessage = chatService.saveMessage(principal.getName(), sessionId, request);

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, savedMessage);
    }


    @GetMapping("/api/club-sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable Integer sessionId) {
        log.info("REST request to get chat history for session {}", sessionId);
        return ResponseEntity.ok(chatService.getSessionHistory(sessionId));
    }
}