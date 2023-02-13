package sobro.webchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomInfo {

    @Id @GeneratedValue
    @Column(name = "CHAT_ROOM_ID")
    private Long id;
    //추후 방 번호로 변경 예정
//    @Column(name = "CHAT_ROOM_NUM")
//    private String roomNum;
    @Column(name = "CHAT_ROOM_NAME")
    private String roomName; // 채팅방 이름
    private int maxUserCnt; // 채팅방 최대 인원 제한
    private String createRoomDate; // 채팅방 생성 날짜
    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부
}
