package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRepository chatRepository;

    @Override
    public List<ChatRoomDto> roomList() {
        return chatRepository.findAllRoom();
    }

    @Override
    public ChatRoomDto createRoom(String roomName, String roomPwd, boolean secret, int maxUserCnt) {
        return chatRepository.createChatRoom(roomName, roomPwd, secret, maxUserCnt);
    }

    @Override
    public ChatRoomDto chatRoomDetail(String roomId) {
        return chatRepository.findRoomById(roomId);
    }

    @Override
    public ArrayList<String> chatUserList(String roomId) {
        return chatRepository.getUserList(roomId);
    }

    @Override
    public String DuplicateName(String roomId, String userName) {
        return chatRepository.isDuplicateName(roomId, userName);
    }

    @Override
    public void roomDelete(String roomId) {
        chatRepository.delChatRoom(roomId);
    }
}
