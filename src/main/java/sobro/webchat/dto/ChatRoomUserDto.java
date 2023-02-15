package sobro.webchat.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ChatRoomUserDto implements Serializable {

    private static final long serialVersionUID = 649467897708900L;

    private String roomId; // 방 번호
    private String userId; //유저 ID
    private String userNick; // 유저 이름
    private String stompId; // 유저에 대한 stompId
    private String createUserEnterDate; // 유저 입장 시간


}