package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.repository.ChatRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;

    @Override
    public void entranceUser(ChatRoomUserDto chatRoomUser) {
        chatRepository.enterChatRoom(chatRoomUser.getRoomId());
        chatRepository.addUser(chatRoomUser);
        chatRepository.plusUserCnt(chatRoomUser.getRoomId());
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
