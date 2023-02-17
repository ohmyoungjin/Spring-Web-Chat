package sobro.webchat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.model.ChatRoom;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


/**
 * view => controller 객체
 */
@Getter
@Setter
@Builder
public class ChatRoomDto {

    private String roomId; // 채팅방 아이디
    private String roomName; // 채팅방 이름
    @Nullable
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한
    private String createRoomDate; // 채팅방 생성 날짜
    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부

    private HashMap<String, ChatRoomUserDto> userList; //채팅방 입장 인원 리스트 key : userId value : 유저 상세정보

    public ChatRoomInfo toEntity() {
        return ChatRoomInfo.builder()
                .roomId(roomId)
                .roomName(roomName)
                .maxUserCnt(maxUserCnt)
                .createRoomDate(createRoomDate)
                .roomPwd(roomPwd)
                .secretChk(secretChk)
                .build();
    }

    public ChatRoom toModel() {
        return ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .maxUserCnt(maxUserCnt)
                .createRoomDate(createRoomDate)
                .roomPwd(roomPwd)
                .secretChk(secretChk)
                .build();
    }

//    public ChatRoomDto fromModel(ChatRoom chatRoom) {
//        return ChatRoomDto.builder()
//                .roomId(chatRoom.getRoomId())
//                .roomName(chatRoom.getRoomName())
//                .maxUserCnt(chatRoom.getMaxUserCnt())
//                .createRoomDate(chatRoom.getCreateRoomDate())
//                .roomPwd(chatRoom.getRoomPwd())
//                .secretChk(chatRoom.isSecretChk())
//                .userCount(chatRoom.getUserCount())
//                .userList()
//                .build();
//    }
}
