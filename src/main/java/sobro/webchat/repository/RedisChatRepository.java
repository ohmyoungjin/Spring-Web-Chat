package sobro.webchat.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import sobro.webchat.dto.ChatMessage;
import sobro.webchat.dto.ChatRoomDto;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.model.ChatRoom;
import sobro.webchat.pubsub.RedisPublisher;
import sobro.webchat.pubsub.RedisSubscriber;

/**
 * redis 관련 소스
 */
@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisChatRepository implements ChatRepository {

    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "TT";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;
    //message 전달 template 정보를 가진 class
    private final RedisPublisher redisPublisher;


    /**
     * redis hash init
     */
    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }
    public ChatRoomDto findTest() {
        try {
            List<ChatRoom> chatRooms = opsHashChatRoom.values(CHAT_ROOMS);
            ChatRoom chatRoom = chatRooms.get(0);
            return ChatRoomDto.builder()
                    .roomId(chatRoom.getRoomId())
                    .roomName(chatRoom.getRoomName())
                    .maxUserCnt(chatRoom.getMaxUserCnt())
                    .secretChk(chatRoom.isSecretChk())
                    .userCount(chatRoom.getUserCount())
                    .build();
        } catch (Exception e){
            return null;
        }

    }
    @Override
    public List<ChatRoom> findAllRoom() {
        try {
            List<ChatRoom> chatRooms = opsHashChatRoom.values(CHAT_ROOMS);
            return chatRooms;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    @Override
    public ChatRoom createChatRoom(ChatRoom chatRoom){

        log.info("ChatDto={}" , chatRoom);
        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    @Override
    public void plusUserCnt(String roomId){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        room.setUserCount(room.getUserCount()+1);
        opsHashChatRoom.put(CHAT_ROOMS, room.getRoomId(), room);
    }

    @Override
    public void minusUserCnt(String roomId){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        room.setUserCount(room.getUserCount()-1);
        opsHashChatRoom.put(CHAT_ROOMS, room.getRoomId(), room);
    }

    @Override
    public boolean chkRoomUserCnt(String roomId){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }

        return true;
    }

    @Override
    public void addUser(ChatRoomUserDto chatRoomUser){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, chatRoomUser.getRoomId());
        log.info("addUserId={}",chatRoomUser.getUserId());
        log.info("addUser={}",chatRoomUser);
        //userList 에 추가
        //room.getUserList().put(chatRoomUser.getUserId(), chatRoomUser);
        opsHashChatRoom.put(CHAT_ROOMS, room.getRoomId(), room);
    }

    @Override
    public String isDuplicateName(String roomId, String username){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String tmp = username;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserList().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp = username+ranNum;
        }

        return tmp;
    }

    @Override
    public void delUser(String roomId, String userID){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        room.getUserList().remove(userID);
        opsHashChatRoom.put(CHAT_ROOMS, room.getRoomId(), room);
    }

    @Override
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        return room.getUserList().get(userUUID).getUserNick();
    }

    @Override
    public ArrayList<ChatRoomUserDto> getUserList(String roomId){
        ArrayList<ChatRoomUserDto> list = new ArrayList<>();

        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        log.info("UserList >>> {}", room);
        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        //room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }

    @Override
    public boolean confirmPwd(String roomId, String roomPwd) {
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        return roomPwd.equals(room.getRoomPwd());
    }

    @Override
    public void delChatRoom(String roomId) {
        try {
            // 채팅방 삭제
            ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
            redisTemplate.opsForHash().delete(CHAT_ROOMS, roomId);

            log.info("삭제 완료 roomId : {}", roomId);
        } catch (Exception e) { // 만약에 예외 발생시 확인하기 위해서 try catch
            log.error("deleChatRoom error={}",e.getMessage());
        }
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    @Override
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    @Override
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }

    @Override
    public void sendMessage(String roomId, ChatMessage message) {
        ChannelTopic topic = getTopic(roomId);
        log.info("sendMessage Data={}", message);
        redisPublisher.publish(topic, message);
    }

    @Override
    public void whisper(String roomId, String targetId, ChatMessage message) {
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String originalMessage = message.getMessage();
        String targetNick = findNickNameById(roomId, targetId);

        //나한테 보내기 (귓속말은 본인과 상대방만 보여야 한다)
        String whisperFrom = room.getUserList().get(message.getSender()).getStompId();
        message.setType(ChatMessage.MessageType.WHISPER);
        message.setTargetId(whisperFrom);
        log.info("whisper sender={}", message.getSender());
        message.setMessage("["+targetNick+"]님에게 귓속말: "+ originalMessage);
        sendMessage(roomId, message);

        //귓속말 메세지 처리
        message.setMessage("["+message.getUserNick()+"]님의 귓속말: "+originalMessage);
        //상대방한테 보내기
        String whisperTo = room.getUserList().get(targetId).getStompId();
        message.setTargetId(whisperTo);
        sendMessage(roomId, message);
    }

    @Override
    public void kickUser(String roomId, String targetId, ChatMessage message) {
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String kickId = room.getUserList().get(targetId).getStompId();
        message.setTargetId(kickId);
        log.info("kickId={}", kickId);
        sendMessage(roomId, message);
        //모두에게 메세지를 보내는 type 설정
        message.setType(ChatMessage.MessageType.TALK);
        sendMessage(roomId, message);
    }

    @Override
    public void delChatRooms() {
        List<ChatRoom> room = opsHashChatRoom.values(CHAT_ROOMS);
        for(int i=0; i<room.size(); i++) {
            redisTemplate.opsForHash().delete(CHAT_ROOMS, room.get(i).getRoomId());
        }
    }

    @Override
    public String findNickNameById(String roomId, String targetId) {
        ChatRoom room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String nickName = room.getUserList().get(targetId).getUserNick();
        return nickName;
    }
}
