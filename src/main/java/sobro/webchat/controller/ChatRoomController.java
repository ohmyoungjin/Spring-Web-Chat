package sobro.webchat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.repository.ChatRoomRepository;
import sobro.webchat.repository.RedisChatRoomRepository;

import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatRoomController {

    private final ChatRoomRepository repository;

    /**
     * 채팅 리스트
     * @param model
     */
    @GetMapping("/")
    public String goChatRoom(Model model){
        model.addAttribute("list", repository.findAllRoom());

        log.info("SHOW ALL ChatList {}", repository.findAllRoom());
        return "roomlist";
    }

    /**
     * 채팅방 생성
     * @param name 방 이름
     * @param roomPwd 방 비밀번호
     * @param secretChk 잠금 여부
     * @param maxUserCnt 참여 인원
     */
    @PostMapping("/chat/createroom")
    public String createRoom(@RequestParam("roomName") String name, @RequestParam("roomPwd")String roomPwd, @RequestParam("secretChk")String secretChk,
                             @RequestParam(value = "maxUserCnt", defaultValue = "100")String maxUserCnt,  RedirectAttributes rttr) {

        // 매개변수 : 방 이름, 패스워드, 방 잠금 여부, 방 인원수
        ChatRoomDto room = repository.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt));
        //kafka topic 생성
        log.info("CREATE Chat Room [{}]", room);

        rttr.addFlashAttribute("roomName", room);
        return "redirect:/";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @GetMapping("/chat/room")
    public String roomDetail(Model model, String roomId){

        log.info("roomId {}", roomId);
        model.addAttribute("room", repository.findRoomById(roomId));
        return "chatroom";
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/chat/userlist")
    @ResponseBody
    public ArrayList<String> userList(String roomId) {
        return repository.getUserList(roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/chat/duplicateName")
    @ResponseBody
    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username) {

        // 유저 이름 확인
        String userName = repository.isDuplicateName(roomId, username);
        log.info("동작확인 {}", userName);

        return userName;
    }

    @GetMapping("/chat/delRoom/{roomId}")
    public String RoomDelete(Model model, @PathVariable String roomId){

        log.info("delete roomId >> " + roomId);
        repository.delChatRoom(roomId);
        return "redirect:/";
    }
}
