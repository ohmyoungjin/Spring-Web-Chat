package sobro.webchat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.pubsub.RedisPublisher;
import sobro.webchat.service.ChatService;

// 채팅을 수신(sub) 하고, 송신(pub) 하기 위한 Controller
// @MessageMapping : 이 어노테이션은 Stomp 에서 들어오는 message 를 서버에서 발송(pub) 한 메시지가 도착하는 엔드포인트이다.
// 여기서 "/chat/enterUser" 로 되어있지만 실제로는 앞에 "/pub" 가 생략되어있다라고 생각하면 된다.
// 즉 클라이언트가 "/pub/chat/enterUser"로 메시지를 발송하면 @MessageMapping 에 의해서 아래의 해당 어노테이션이 달린 메서드가 실행된다.
// convertAndSend() : 이 메서드는 매개변수로 각각 메시지의 도착 지점과 객체를 넣어준다.
// 이를 통해서 도착 지점 즉 sub 되는 지점으로 인자로 들어온 객체를 Message 객체로 변환해서
// 해당 도작지점을 sub 하고 있는 모든 사용자에게 메시지를 보내주게 된다.
@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅방 입장
     */
    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {

        // 채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = chatService.entranceUser(message.getRoomId(), message.getSender());
        // 반환 결과를 socket session 에 userUUID 로 저장
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());
        message.setMessage(message.getSender() + " 님 입장!!");

        chatService.sendMessage(message.getRoomId(), message);
    }

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        log.info("CHAT {}", message);
        message.setMessage(message.getMessage());
        chatService.sendMessage(message.getRoomId(), message);
    }

    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("DisConnEvent {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("headAccessor {}", headerAccessor);


        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
        String username = chatService.userLeave(roomId, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            ChatMessage chat = ChatMessage.builder()
                    .type(ChatMessage.MessageType.LEAVE)
                    .sender(username)
                    .message(username + " 님 퇴장!!")
                    .roomId(roomId)
                    .build();
            chatService.sendMessage(chat.getRoomId(), chat);
        }
    }
}
