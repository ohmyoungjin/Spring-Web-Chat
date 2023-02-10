package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import sobro.webchat.repository.ChatRoomRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public String entranceUser(String roomId, String sender) {
        chatRoomRepository.enterChatRoom(roomId);
        String userUUID = chatRoomRepository.addUser(roomId, sender);
        chatRoomRepository.plusUserCnt(roomId);
        return userUUID;
    }

    @Override
    public ChannelTopic selectTopic(String roomId) {
        ChannelTopic topic = chatRoomRepository.getTopic(roomId);
        return topic;
    }

    @Override
    public String userLeave(String roomId, String userId) {
        chatRoomRepository.minusUserCnt(roomId);
        String userName = chatRoomRepository.getUserName(roomId, userId);
        chatRoomRepository.delUser(roomId, userId);
        return userName;
    }
}
