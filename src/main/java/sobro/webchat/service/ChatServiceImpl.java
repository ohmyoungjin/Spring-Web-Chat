package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.entity.ChatRoomUserInfo;
import sobro.webchat.repository.ChatInfoRepository;
import sobro.webchat.repository.ChatRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final ChatInfoRepository chatInfoRepository;


    @Override
    @Transactional
    public void entranceUser(String UUID, ChatMessage message) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatedNow = now.format(formatter);

        // 채팅에 들어온 유저 세팅
        ChatRoomUserDto chatRoomUser = ChatRoomUserDto.builder()
                .roomId(message.getRoomId())
                .userId(message.getSender())
                .stompId(UUID)
                .userNick(message.getUserNick())
                .createUserEnterDate(formatedNow)
                .build();

        chatRepository.enterChatRoom(chatRoomUser.getRoomId());
        chatRepository.addUser(chatRoomUser);
        chatRepository.plusUserCnt(chatRoomUser.getRoomId());

        //입장한 방에 대한 정보
        ChatRoomInfo chatRoom = chatInfoRepository.findRoomById(chatRoomUser.getRoomId());
        ChatRoomUserInfo chatRoomUserInfo = chatRoomUser.toEntity(chatRoom);
        chatInfoRepository.enterUser(chatRoomUserInfo);
    }

    @Override
    public void sendMessage(String roomId, ChatMessage chatMessage) {
        chatRepository.sendMessage(roomId, chatMessage);
    }

    @Override
    public String userLeave(String roomId, String userId) {
        chatRepository.minusUserCnt(roomId);
        String userName = chatRepository.getUserName(roomId, userId);
        chatRepository.delUser(roomId, userId);
        return userName;
    }

    @Override
    public void whisper(String roomId, String targetId, ChatMessage chatMessage) {
        chatRepository.whisper(roomId, targetId, chatMessage);
    }

    @Override
    public void kickUser(String roomId, String targetId, ChatMessage chatMessage) {
        chatRepository.kickUser(roomId, targetId, chatMessage);
    }

}
