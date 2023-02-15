package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.repository.ChatInfoRepository;
import sobro.webchat.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRepository chatRepository;

    private final ChatInfoRepository chatInfoRepository;

    @Override
    public List<ChatRoomDto> roomList() {
        return chatRepository.findAllRoom();
    }

    @Override
    @Transactional
    public ChatRoomDto createRoom(String roomName, String roomPwd, boolean secret, int maxUserCnt) {
        //방 init
        ChatRoomDto chatRoom = chatRepository.createChatRoom(roomName, roomPwd, secret, maxUserCnt);
        //방 정보 DB 저장
        chatInfoRepository.insertChatRoomInfo(chatRoom);
        return chatRoom;
    }

    @Override
    public ChatRoomDto chatRoomDetail(String roomId) {
        return chatRepository.findRoomById(roomId);
    }

    @Override
    public ArrayList<ChatRoomUserDto> chatUserList(String roomId) {
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
