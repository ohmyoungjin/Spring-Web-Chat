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
    public void createChatRoomInfo(ChatRoomInfo chatRoomInfo) {
        em.persist(chatRoomInfo);
    }

    public ChatRoomInfo findRoomById(String roomId) {
        List<ChatRoomInfo> chatRoomInfoList = em.createQuery("select c from ChatRoomInfo c where c.roomNum = :roomId", ChatRoomInfo.class)
                .setParameter("roomId", roomId)
                .getResultList();
        log.info("찾은 roomNum={}  : ", chatRoomInfoList.get(0).getRoomNum());
        return chatRoomInfoList.get(0);
    }

    @Override
    public void enterUser(ChatRoomUserInfo chatRoomUserInfo) {
        em.persist(chatRoomUserInfo);
    }
}
