package sobro.webchat.repository;

import sobro.webchat.dto.ChatRoomDto;

public interface ChatInfoRepository {
    /**
     * 방 생성 DB Insert
     * @param chatRoomDto
     */
    void insertChatRoomInfo(ChatRoomDto chatRoomDto);
}
