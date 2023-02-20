package sobro.webchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.repository.ChatInfoRepository;
import sobro.webchat.repository.ChatRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    public void createRoom(String roomName, String roomPwd, boolean secret, int maxUserCnt) {
        // roomName 와 roomPwd 로 chatRoom 빌드 후 return
        // 현재 날짜 구하기
        LocalDate now = LocalDate.now();
        // 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatedNow = now.format(formatter);

        //방 init
        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPwd(roomPwd) // 채팅방 패스워드
                .secretChk(secret) // 채팅방 잠금 여부
                .userCount(0) // 채팅방 참여 인원수
                .maxUserCnt(maxUserCnt) // 최대 인원수 제한
                .userList(new HashMap<String, ChatRoomUserDto>())
                .createRoomDate(formatedNow) //방 생성 날짜
                .build();
        chatRepository.createChatRoom(chatRoomDto);
        //방 정보 DB 저장
        ChatRoomInfo chatRoomInfo = chatRoomDto.toEntity();
        chatInfoRepository.createChatRoomInfo(chatRoomInfo);
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
    public void deleteRoom(String roomId) {
        chatRepository.delChatRoom(roomId);
    }

    @Override
    public void deleteRooms() {
        chatRepository.delChatRooms();
    }
}
