package sobro.webchat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.service.ChatRoomService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅 리스트
     * @param model
     */
    @GetMapping("/")
    public String getRoomList(Model model){
        List<ChatRoomDto> roomList = chatRoomService.roomList();
        model.addAttribute("list", roomList);

        log.info("SHOW ALL ChatList {}", roomList);
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
        ChatRoomDto room = chatRoomService.createRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt));

        log.info("CREATE Chat Room [{}]", room);

        rttr.addFlashAttribute("roomName", room);
        return "redirect:/";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    // 테스트에 필요한 용도 삭제 예정
    @GetMapping("/chat/room/{roomId}")
    public String roomDetail(@PathVariable  String roomId, Model model){
        ChatRoomDto room = chatRoomService.chatRoomDetail(roomId);
        log.info("roomId {}", roomId);
        model.addAttribute("room", room);
        return "chatroom";
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/chat/userList/{roomId}")
    @ResponseBody
    public ArrayList<ChatRoomUserDto> getUserListInRoom(@PathVariable String roomId) {
        return chatRoomService.chatUserList(roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/chat/duplicateName/{roomId}")
    @ResponseBody
    public String hasDuplicateNameInRoom(@PathVariable String roomId, @RequestParam("username") String username) {

        // 유저 이름 확인
        String userName = chatRoomService.DuplicateName(roomId, username);
        log.info("동작확인 {}", userName);

        return userName;
    }

    @GetMapping("/chat/delRoom/{roomId}")
    public String deleteRoom(Model model, @PathVariable String roomId){
        chatRoomService.roomDelete(roomId);
        log.info("채팅방 삭제={}", roomId);
        return "redirect:/";
    }
}
