package sobro.webchat.service;

import org.springframework.data.redis.listener.ChannelTopic;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.dto.ChatRoomUserDto;

public interface ChatService {

    /**
     * 채팅방 입장
     * @param chatRoomUserDto 메세지 보낸 유저에 대한 정보
     * @return
     */
    void entranceUser(ChatRoomUserDto chatRoomUserDto);


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

    /**
     * 강제 퇴장
     * @param roomId
     * @param targetId
     * @param chatMessage
     */
    void kickUser(String roomId, String targetId, ChatMessage chatMessage);
}
