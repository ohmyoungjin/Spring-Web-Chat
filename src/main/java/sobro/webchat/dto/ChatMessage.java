package sobro.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    // 메시지  타입 : 입장, 채팅, 퇴장, 귓속말, 강제퇴장
    // 메시지 타입에 따라서 동작하는 구조가 달라진다.
    // 입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
    // TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.
    public enum MessageType{
        ENTER, TALK, LEAVE, WHISPER, KICK
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람 Id
    private String userNick; // 채팅을 보낸 사람 userName
    private String senderStompId; // 채팅을 보낸 사람에 대한 stomp Id
    private String message; // 메시지
    private String time; // 채팅 발송 시간
    // NULLABLE로 특정사용자에 대해 이벤트를 발생시키기 위한 값.
    private String targetId;
    private String targetNick;
}
