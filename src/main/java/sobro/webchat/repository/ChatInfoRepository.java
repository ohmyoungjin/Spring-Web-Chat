package sobro.webchat.repository;

import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;

public interface ChatInfoRepository {
    /**
     * 방 생성 DB Insert
     * @param chatRoomDto
     */
    void createChatRoomInfo(ChatRoomDto chatRoomDto);

    /**
     *  방 찾기
     */
    ChatRoomInfo findRoomById(String roomId);

    /**
     * 유저 입장 DB Insert
     */
    void enterUser(ChatRoomUserDto chatRoomUserDto);
}
