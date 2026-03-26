package dto;

public record SendMessagePostReply(String chat_id, String text, long reply_to_message_id) {
}
