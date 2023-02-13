package sobro.webchat.service;

import org.springframework.data.redis.listener.ChannelTopic;
import sobro.webchat.dto.ChatMessage;

public interface ChatService {

    /**
     * 채팅방 입장
     * @param roomId
     * @param sender
     * @return
     */
    String entranceUser(String roomId, String sender, String UUID);


    /**
     * 메세지 보내기
     * @param roomId
     * @return
     */
    void sendMessage(String roomId, ChatMessage chatMessage);

    /**
     * 채팅방 퇴장
     * @param roomId
     * @param userId
     * @return
     */
    String userLeave(String roomId, String userId);

    /**
     * 귓속말 보내기
     */
    void whisper(String roomId, String targetId, ChatMessage chatMessage);
}
