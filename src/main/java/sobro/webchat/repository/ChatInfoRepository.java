package sobro.webchat.repository;

import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.entity.ChatRoomUserInfo;

public interface ChatInfoRepository {
    /**
     * 방 생성 DB Insert
     * @param chatRoomInfo
     */
    void createChatRoomInfo(ChatRoomInfo chatRoomInfo);

    /**
     *  방 찾기
     */
    ChatRoomInfo findRoomById(String roomId);

    /**
     * 유저 입장 DB Insert
     */
    void enterUser(ChatRoomUserInfo chatRoomUserInfo);
}
