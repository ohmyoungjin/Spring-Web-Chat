package sobro.webchat.service;

import org.springframework.data.redis.listener.ChannelTopic;

public interface ChatService {

    String entranceUser(String roomId, String sender);

    ChannelTopic selectTopic(String roomId);

    String userLeave(String roomId, String userId);

}
