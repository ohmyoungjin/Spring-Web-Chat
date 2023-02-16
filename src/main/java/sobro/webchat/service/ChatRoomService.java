package sobro.webchat.service;

import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;

import java.util.ArrayList;
import java.util.List;

public interface ChatRoomService {

    /**
     * 전체 채팅방 리스트
     */
    List<ChatRoomDto> roomList();

    /**
     * 방 생성
     */
    ChatRoomDto createRoom(String roomName, String roomPwd, boolean secret, int maxUserCnt);

    /**
     * 채팅방 입장 화면
     * @param roomId 방 ID
     */
    ChatRoomDto chatRoomDetail(String roomId);

    /**
     * 채팅방 유저 목록
     * @param roomId 방 ID
     * @return
     */
    ArrayList<ChatRoomUserDto> chatUserList(String roomId);

    /**
     * 유저 아이디 검증
     * 추후 repo로 이동 후 삭제 예정
     */
    String DuplicateName(String roomId, String userName);

    /**
     * 선택된 방 채팅방 삭제
     * @param roomId
     * @return
     */
    void deleteRoom(String roomId);

    /**
     * 채팅방 삭제
     * @return
     */
    void deleteRooms();
}
