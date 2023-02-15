package sobro.webchat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


/**
 * REDIS 저장되는 방에 대한 DTO
 * serialVersionUID 로 해시 코드 생성
 */
@Data
@Builder
public class ChatRoomDto implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId; // 채팅방 아이디
    private String roomName; // 채팅방 이름 
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한
    private String createRoomDate; // 채팅방 생성 날짜
    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부

    private HashMap<String, ChatRoomUserDto> userList; //채팅방 입장 인원 리스트 key : userId value : 유저 상세정보

}
