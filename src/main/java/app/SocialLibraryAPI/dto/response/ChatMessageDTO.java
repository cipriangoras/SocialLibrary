package app.SocialLibraryAPI.dto.response;

import java.time.LocalDateTime;

public record ChatMessageDTO(
        Integer id,
        String content,
        LocalDateTime sentAt,
        Long senderId,
        String senderName,
        String senderProfilePicUrl,
        Integer sessionId
) {}