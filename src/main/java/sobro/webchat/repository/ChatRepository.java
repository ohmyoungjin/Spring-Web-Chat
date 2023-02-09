package sobro.webchat.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;
import sobro.webchat.dto.ChatRoomDto;

import javax.annotation.PostConstruct;
import java.util.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import sobro.webchat.pubsub.RedisSubscriber;

// 추후 DB 와 연결 시 Service 와 Repository(DAO) 로 분리 예정
@RequiredArgsConstructor
@Repository
@Slf4j
public class ChatRepository {

    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "TEST_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomDto> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    private Map<String, ChatRoomDto> chatRoomMap;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }

    /**
     * redis hash init
     */
    @PostConstruct
    private void init() {
        System.out.println("여기먼저 타나요!?");
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoomDto> findAllRoom() {
        System.out.println("리턴전");
        List<ChatRoomDto> chatRooms = opsHashChatRoom.values(CHAT_ROOMS);
        System.out.println("리턴후");
        return chatRooms;
    }


    public ChatRoomDto findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    // roomName 로 채팅방 만들기
    public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt){
        // roomName 와 roomPwd 로 chatRoom 빌드 후 return

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPwd(roomPwd) // 채팅방 패스워드
                .secretChk(secretChk) // 채팅방 잠금 여부
                .userlist(new HashMap<String, String>())
                .userCount(0) // 채팅방 참여 인원수
                .maxUserCnt(maxUserCnt) // 최대 인원수 제한
                .build();
        log.info("ChatDto={}" , chatRoomDto);
        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장장
        opsHashChatRoom.put(CHAT_ROOMS, chatRoomDto.getRoomId(), chatRoomDto);
        System.out.println("방 만들어서 넣었습니다");
        return chatRoomDto;
    }


    /**
     * 채팅방 인원 +1
     * @param roomId
     */
    public void plusUserCnt(String roomId){
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        room.setUserCount(room.getUserCount()+1);
    }

    // 채팅방 인원-1
//    public void minusUserCnt(String roomId){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        room.setUserCount(room.getUserCount()-1);
//    }

    public void minusUserCnt(String roomId){
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        room.setUserCount(room.getUserCount()-1);
    }

    // maxUserCnt 에 따른 채팅방 입장 여부
//    public boolean chkRoomUserCnt(String roomId){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//
//        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());
//
//        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
//            return false;
//        }
//
//        return true;
//    }

    public boolean chkRoomUserCnt(String roomId){
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());

        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
            return false;
        }

        return true;
    }

    // 채팅방 유저 리스트에 유저 추가
//    public String addUser(String roomId, String userName){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        String userUUID = UUID.randomUUID().toString();
//
//        // 아이디 중복 확인 후 userList 에 추가
//        room.getUserlist().put(userUUID, userName);
//
//        return userUUID;
//    }

    public String addUser(String roomId, String userName){
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String userUUID = UUID.randomUUID().toString();

        // 아이디 중복 확인 후 userList 에 추가
        room.getUserlist().put(userUUID, userName);

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
//    public String isDuplicateName(String roomId, String username){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        String tmp = username;
//
//        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
//        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
//        while(room.getUserlist().containsValue(tmp)){
//            int ranNum = (int) (Math.random()*100)+1;
//
//            tmp = username+ranNum;
//        }
//
//        return tmp;
//    }

    public String isDuplicateName(String roomId, String username){
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        String tmp = username;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserlist().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp = username+ranNum;
        }

        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoomDto room = chatRoomMap.get(roomId);
        room.getUserlist().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoomDto room = chatRoomMap.get(roomId);
        return room.getUserlist().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId){
        ArrayList<String> list = new ArrayList<>();

        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 reutrn
        room.getUserlist().forEach((key, value) -> list.add(value));
        return list;
    }

    // 채팅방 비밀번호 조회
    public boolean confirmPwd(String roomId, String roomPwd) {
        ChatRoomDto room = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        return roomPwd.equals(room.getRoomPwd());
//        String pwd = chatRoomMap.get(roomId).getRoomPwd();
        //return roomPwd.equals(chatRoomMap.get(roomId).getRoomPwd());
    }

    // 채팅방 삭제
    public void delChatRoom(String roomId) {
        try {
            // 채팅방 삭제
            chatRoomMap.remove(roomId);

            // 채팅방 안에 있는 파일 삭제
//            fileService.deleteFileDir(roomId);

            log.info("삭제 완료 roomId : {}", roomId);

        } catch (Exception e) { // 만약에 예외 발생시 확인하기 위해서 try catch
            System.out.println(e.getMessage());
        }
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
