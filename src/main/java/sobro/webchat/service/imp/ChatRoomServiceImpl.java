package sobro.webchat.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.model.ChatRoom;
import sobro.webchat.model.ChatRoomUser;
import sobro.webchat.repository.ChatInfoRepository;
import sobro.webchat.repository.ChatRepository;
import sobro.webchat.service.ChatRoomService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRepository chatRepository;

    private final ChatInfoRepository chatInfoRepository;

    @Override
    public List<ChatRoomDto> roomList() {
        return chatRepository.findAllRoom()
                .stream().map(ChatRoomDto::fromModel).collect(Collectors.toList());
        //return chatRepository.findAllRoom();
    }

    @Override
    @Transactional
    public ChatRoom createRoom(ChatRoomDto chatRoomDto) {
        // 현재 날짜 구하기 추후 파라미터로 받아서 삭제 예정
        LocalDate now = LocalDate.now();
        // 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatedNow = now.format(formatter);

        //redis 방 정보 init
        ChatRoom chatRoom = chatRoomDto.toModel();
        chatRepository.createChatRoom(chatRoom);

        //jpa 방 정보 init
        ChatRoomInfo chatRoomInfo = ChatRoomInfo.builder()
                .roomName(chatRoomDto.getRoomName())
                .roomId(chatRoomDto.getRoomId())
                .maxUserCnt(chatRoomDto.getMaxUserCnt())
                .createRoomDate(chatRoomDto.getCreateRoomDate())
                .roomPwd(chatRoomDto.getRoomPwd())
                .secretChk(chatRoomDto.isSecretChk())
                .build();

        //방 정보 DB 저장
        //chatInfoRepository.createChatRoomInfo(chatRoomInfo);
        return chatRoom;
    }

    @Override
    public ChatRoomDto chatRoomDetail(String roomId) {
        ChatRoom roomById = chatRepository.findRoomById(roomId);
        return ChatRoomDto.builder()
                .roomId(roomById.getRoomId())
                .roomName(roomById.getRoomName())
                .roomPwd(roomById.getRoomPwd())
                .maxUserCnt(roomById.getMaxUserCnt())
                .userCount(roomById.getUserCount())
                .build();
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
