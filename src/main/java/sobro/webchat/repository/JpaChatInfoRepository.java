package sobro.webchat.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.entity.ChatRoomInfo;
import sobro.webchat.entity.ChatRoomUserInfo;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaChatInfoRepository implements ChatInfoRepository{

    private final EntityManager em;


    @Override
    public void createChatRoomInfo(ChatRoomDto chatRoomDto) {
        ChatRoomInfo chatRoomInfo = ChatRoomInfo.builder()
                .roomName(chatRoomDto.getRoomName())
                .roomNum(chatRoomDto.getRoomId())
                .maxUserCnt(chatRoomDto.getMaxUserCnt())
                .createRoomDate(chatRoomDto.getCreateRoomDate())
                .roomPwd(chatRoomDto.getRoomPwd())
                .secretChk(chatRoomDto.isSecretChk())
                .build();
        em.persist(chatRoomInfo);
    }

    public ChatRoomInfo findRoomById(String roomId) {
        List<ChatRoomInfo> chatRoomInfoList = em.createQuery("select c from ChatRoomInfo c where c.roomNum = :roomId", ChatRoomInfo.class)
                .setParameter("roomId", roomId)
                .getResultList();
        log.info("찾은 roomNum={}  : ", chatRoomInfoList.get(0));
        return chatRoomInfoList.get(0);
    }

    @Override
    public void enterUser(ChatRoomUserDto chatRoomUserDto) {
        ChatRoomInfo chatRoomInfo = findRoomById(chatRoomUserDto.getRoomId());

        ChatRoomUserInfo chatRoomUserInfo = ChatRoomUserInfo.builder()
                .chatRoomInfo(chatRoomInfo)
                .userId(chatRoomUserDto.getUserId())
                .stompId(chatRoomUserDto.getStompId())
                .userNick(chatRoomUserDto.getUserNick())
                .enterUserDate(chatRoomUserDto.getCreateUserEnterDate())
                .build();
        em.persist(chatRoomUserInfo);
    }
}
