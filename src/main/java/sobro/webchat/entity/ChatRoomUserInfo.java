package sobro.webchat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHAT_ROOM_USER_INFO")
public class ChatRoomUserInfo {

    @Id @GeneratedValue
    @Column(name = "CHAT_ROOM_USER_ID")
    private Long id;
    @Column(name = "CHAT_ROOM_USER_SESSION")
    private String stompId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_ID")
    private ChatRoomInfo chatRoomInfo;

    private String userId;
    private String userNick;

    private String enterUserDate; // 채팅방 입장 날짜
    private String userLevel; // 유저 등급
}
