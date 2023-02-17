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
@Table(name = "CHAT_ROOM_USER")
public class ChatRoomUserInfo {

    @Id @GeneratedValue
    @Column(name = "CHAT_ROOM_USER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ROOM_NUM")
    private ChatRoomInfo chatRoomInfo;

    private String userId;
    private String userNick;

    private String enterUserDate; // 채팅방 입장 날짜
    private String userLevel; // 유저 등급
}
