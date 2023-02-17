package sobro.webchat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sobro.webchat.dto.ChatRoomDto;
import sobro.webchat.dto.ChatRoomUserDto;
import sobro.webchat.model.ChatRoom;
import sobro.webchat.repository.RedisChatRepository;
import sobro.webchat.service.ChatRoomService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final RedisChatRepository repository;
    /**
     * 채팅 리스트
     * @param model
     */
    @GetMapping("/")
    public String getRoomList(Model model){
        List<ChatRoomDto> roomList = chatRoomService.roomList();
        //ChatRoomDto roomList = repository.findTest();
        log.info("SHOW ALL ChatList {}", roomList);
        model.addAttribute("list", roomList);


        return "roomlist";
    }

    /**
     * 채팅방 생성
     * @param chatRoomDto
     * @param rttr
     * @return
     */
    @PostMapping("/chat/createroom")
    public String createRoom(@ModelAttribute("ChatRoomDto") ChatRoomDto chatRoomDto, RedirectAttributes rttr) {
        // 매개변수 : 방 이름, 패스워드, 방 잠금 여부, 방 인원수
        log.info("CreateChatRoomDto={}", chatRoomDto);
        ChatRoom room = chatRoomService.createRoom(chatRoomDto);

        log.info("CREATE Chat Room [{}]", room);

        //rttr.addFlashAttribute("roomName", room);
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

    /**
     * 채팅에 참여한 유저 리스트 반환
     * @param roomId 방 이름
     */
    @GetMapping("/chat/userList/{roomId}")
    @ResponseBody
    public ArrayList<ChatRoomUserDto> getUserListInRoom(@PathVariable String roomId) {
        return chatRoomService.chatUserList(roomId);
    }

    /**
     * 채팅에 참여한 유저 닉네임 중복 확인
     * @param roomId
     * @param username
     * @return
     */
    @GetMapping("/chat/duplicateName/{roomId}")
    @ResponseBody
    public String hasDuplicateNameInRoom(@PathVariable String roomId, @RequestParam("username") String username) {

        // 유저 이름 확인
        String userName = chatRoomService.DuplicateName(roomId, username);
        log.info("동작확인 {}", userName);

        return userName;
    }

    /**
     * 선택된 방 채팅방 삭제
     * @param model
     * @param roomId
     * @return
     */
    @GetMapping("/chat/delRoom/{roomId}")
    public String deleteRoom(Model model, @PathVariable String roomId){
        chatRoomService.deleteRoom(roomId);
        log.info("채팅방 삭제={}", roomId);
        return "redirect:/";
    }

    /**
     * 채팅방 모두 삭제
     * @return
     */
    @GetMapping("/chat/delAllRoom")
    public String deleteRooms(){
        chatRoomService.deleteRooms();
        log.info("채팅방 전체 삭제");
        return "redirect:/";
    }
}
