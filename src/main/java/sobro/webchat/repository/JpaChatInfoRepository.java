package sobro.webchat.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.entity.ChatRoomInfo;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class JpaChatInfoRepository implements ChatInfoRepository{

    private final EntityManager em;

    @Override
    public void insertChatRoomInfo(ChatRoomDto chatRoomDto) {
        ChatRoomInfo chatRoomInfo = ChatRoomInfo.builder()
                .roomName(chatRoomDto.getRoomName())
                .maxUserCnt(chatRoomDto.getMaxUserCnt())
                .createRoomDate(chatRoomDto.getCreateRoomDate())
                .roomPwd(chatRoomDto.getRoomPwd())
                .secretChk(chatRoomDto.isSecretChk())
                .build();
        em.persist(chatRoomInfo);
    }
}
