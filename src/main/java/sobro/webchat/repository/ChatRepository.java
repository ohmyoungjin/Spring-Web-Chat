package sobro.webchat.repository;

import org.springframework.data.redis.listener.ChannelTopic;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.dto.ChatRoomDto;

import java.util.ArrayList;
import java.util.List;

public interface ChatRepository {
    /**
     * 모든 채팅방 리스트 확인
     */
    List<ChatRoomDto> findAllRoom();
    /**
     * 이름으로 방 찾기
     */
    ChatRoomDto findRoomById(String id);
    /**
     * 채팅방 생성
     * @param roomName 방 이름
     * @param roomPwd 방 비밀번호
     * @param secretChk 방 잠금 여부
     * @param maxUserCnt 방 최대 인원
     * @return
     */
    ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt);
    /**
     * 채팅방 인원수 증가
     * @param roomId
     */
    void plusUserCnt(String roomId);
    /**
     * 채팅방 인원 감소
     * @param roomId
     */
    void minusUserCnt(String roomId);

    /**
     * max 참여자 확인 및 입장 여부
     * @param roomId
     * @return
     */
    boolean chkRoomUserCnt(String roomId);
    /**
     * 채팅방 유저 리스트 추가
     * @param roomId
     * @param userName
     * @return
     */
    String addUser(String roomId, String userName, String UUID);

    /**
     * 채팅방 유저 닉네임 중복 체크
     * @param roomId
     * @param username
     * @return
     */
    String isDuplicateName(String roomId, String username);

    /**
     * 채팅방 유저 삭제
     * @param roomId
     * @param userUUID
     */
    void delUser(String roomId, String userUUID);

    /**
     * 채팅방 userName 조회
     * @param roomId
     * @param userUUID
     * @return
     */
    String getUserName(String roomId, String userUUID);

    /**
     * 채팅방 전체 userlist 조회
     * @param roomId
     * @return
     */
    ArrayList<String> getUserList(String roomId);

    /**
     * 채팅방 비밀번호 조회
     * @param roomId
     * @param roomPwd
     * @return
     */
    boolean confirmPwd(String roomId, String roomPwd);

    /**
     * 채팅방 유저 삭제
     * @param roomId
     */
    void delChatRoom(String roomId);

    /**
     * 채팅방 입장
     * @param roomId
     */
    void enterChatRoom(String roomId);

    /**
     * 채팅방 Topic 조회
     * @param roomId
     * @return
     */
    ChannelTopic getTopic(String roomId);

    /**
     * 채팅 보내기
     * @param roomId
     * @param message
     */
    void sendMessage(String roomId, ChatMessage message);

}