package dto;

public record ForwardMessage(String chat_id, long from_chat_id, long message_id, String caption) {
}
