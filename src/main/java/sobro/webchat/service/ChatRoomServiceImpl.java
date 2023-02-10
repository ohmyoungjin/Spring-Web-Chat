package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.repository.ChatRoomRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public List<ChatRoomDto> roomList() {
        return chatRoomRepository.findAllRoom();
    }

    @Override
    public ChatRoomDto createRoom(String roomName, String roomPwd, boolean secret, int maxUserCnt) {
        return chatRoomRepository.createChatRoom(roomName, roomPwd, secret, maxUserCnt);
    }

    @Override
    public ChatRoomDto chatRoomDetail(String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    @Override
    public ArrayList<String> chatUserList(String roomId) {
        return chatRoomRepository.getUserList(roomId);
    }

    @Override
    public String DuplicateName(String roomId, String userName) {
        return chatRoomRepository.isDuplicateName(roomId, userName);
    }

    @Override
    public void roomDelete(String roomId) {
        chatRoomRepository.delChatRoom(roomId);
    }
}
