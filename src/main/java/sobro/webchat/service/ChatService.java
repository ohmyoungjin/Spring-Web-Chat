package sobro.webchat.service;

import org.springframework.data.redis.listener.ChannelTopic;

public interface ChatService {

    /**
     * 채팅방 입장
     * @param roomId
     * @param sender
     * @return
     */
    String entranceUser(String roomId, String sender);


    /**
     * 접속해있는 Redis Topic 구하기
     * @param roomId
     * @return
     */
    ChannelTopic selectTopic(String roomId);

    /**
     * 채팅방 퇴장
     * @param roomId
     * @param userId
     * @return
     */
    String userLeave(String roomId, String userId);

}
