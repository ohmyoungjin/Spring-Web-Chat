'use strict';

// document.write("<script src='jquery-3.6.1.js'></script>")
document.write("<script\n" +
    "  src=\"https://code.jquery.com/jquery-3.6.1.min.js\"\n" +
    "  integrity=\"sha256-o88AwQnZB+VDvE9tvIXrMQaPlFFSUTR+nldQm1LuPXQ=\"\n" +
    "  crossorigin=\"anonymous\"></script>")


var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
let targetId = null;
let userId = null;
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

// roomId 파라미터 가져오기 restApi 변경
//const url = new URL(location.href).searchParams;
//const roomId = url.get('roomId');
const url = new URL(location.href).pathname;
const roomId = url.replace('/chat/room/','');



function connect(event) {
    console.log("connect");
    username = document.querySelector('#name').value.trim();
    userId = document.querySelector('#userId').value.trim();

    // username 중복 확인
    //isDuplicateName();

    // usernamePage 에 hidden 속성 추가해서 가리고
    // chatPage 를 등장시킴
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    // 연결하고자하는 Socket 의 endPoint
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);


    event.preventDefault();


}

function onConnected() {
    console.log("onConnected url : ", roomId);
    // sub 할 url => /sub/chat/room/roomId 로 구독한다
    stompClient.subscribe('/sub/chat/room/' + roomId, onMessageReceived);
    // 귓속말 할 url
    stompClient.subscribe('/user/queue', onMessageReceived);
    // 서버에 username 을 가진 유저가 들어왔다는 것을 알림
    // /pub/chat/enterUser 로 메시지를 보냄
    stompClient.send("/pub/chat/enterUser",
        {},
        JSON.stringify({
            "roomId": roomId,
            sender: userId,
            userNick: username,
            type: 'ENTER'
        })
    )

    connectingElement.classList.add('hidden');

}

// 유저 닉네임 중복 확인
function isDuplicateName() {
    console.log("isDuplicateName");
    $.ajax({
        type: "GET",
        url: "/chat/duplicateName/"+roomId,
        data: {
            "username": username,
            "roomId": roomId
        },
        success: function (data) {
            console.log("함수 동작 확인 : " + data);

            username = data;
        }
    })

}

// 유저 리스트 받기
// ajax 로 유저 리스를 받으며 클라이언트가 입장/퇴장 했다는 문구가 나왔을 때마다 실행된다.
function getUserList() {
    console.log("getUserList");
    const $list = $("#list");

    $.ajax({
        type: "GET",
        url: "/chat/userList/"+roomId,
        data: {
            "roomId": roomId
        },
        success: function (data) {
            var users = "";
            for (let i = 0; i < data.length; i++) {
                //console.log("data[i] : "+data[i]);
                users += "<li class='dropdown-item'>" + data[i].userNick + "</li>"
            }
            $list.html(users);
        }
    })
}

function getTargetUserList() {
    console.log("getTargetUserList");
    let $targetId = $("#targetId");

    $.ajax({
        type: "GET",
        url: "/chat/userList/"+roomId,
        data: {
            "roomId": roomId
        },
        success: function (data) {
            console.log('data : ', data);
            var targetUsers = "";
            targetUsers += "<option class='target' value='all'>모두</option>"
            for (let i = 0; i < data.length; i++) {
                if(data[i].userNick != username){
                    targetUsers += "<option class='target' value='" + data[i].userId + "'>" + data[i].userNick + "</option>"
                }
            }
            $targetId.html(targetUsers)
        }
    })
}

function onError(error) {
    console.log("onError");
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

// 메시지 전송때는 JSON 형식을 메시지를 전달한다.
function sendMessage(event) {
    console.log(">>>>>>>>>> sendMessage", targetId);
    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        if (targetId == null || targetId == 'all') {
            var chatMessage = {
                "roomId": roomId,
                sender: userId,
                userNick: username,
                message: messageInput.value,
                type: 'TALK'
            };
            stompClient.send("/pub/chat/sendMessage", {}, JSON.stringify(chatMessage));
        } else {
            var chatMessage = {
                "roomId": roomId,
                sender: userId,
                userNick: username,
                message: messageInput.value,
                targetId: targetId,
                type: 'WHISPER'
            };
            stompClient.send("/pub/chat/whisperMessage", {}, JSON.stringify(chatMessage));
            //강제퇴장
            //stompClient.send("/pub/chat/kickUser", {}, JSON.stringify(chatMessage));
        }




        messageInput.value = '';
    }
    event.preventDefault();
}

// 메시지를 받을 때도 마찬가지로 JSON 타입으로 받으며,
// 넘어온 JSON 형식의 메시지를 parse 해서 사용한다.
function onMessageReceived(payload) {
    console.log("onMessageReceived");
    var chat = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    console.log(chat.type)
    if (chat.type === 'ENTER') {  // chatType 이 enter 라면 아래 내용
        messageElement.classList.add('event-message');
        chat.content = chat.sender + chat.message;
        getUserList();
        getTargetUserList();

    } else if (chat.type === 'LEAVE') { // chatType 가 leave 라면 아래 내용
        messageElement.classList.add('event-message');
        chat.content = chat.sender + chat.message;
        getUserList();
        getTargetUserList();

    } else if (chat.type === 'WHISPER') { // chatType 이 talk 라면 아래 내용
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(chat.userNick[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(chat.userNick);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(chat.userNick);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    } else if (chat.type === 'KICK') {
        alert("KICK!!!!");
        stompClient.disconnect();
        location.href="/"
    } else {
        console.log("ELSE!!");
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(chat.userNick[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(chat.userNick);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(chat.userNick);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var contentElement = document.createElement('p');

    // 만약 s3DataUrl 의 값이 null 이 아니라면 => chat 내용이 파일 업로드와 관련된 내용이라면
    // img 를 채팅에 보여주는 작업
    if(chat.s3DataUrl != null){
        var imgElement = document.createElement('img');
        imgElement.setAttribute("src", chat.s3DataUrl);
        imgElement.setAttribute("width", "300");
        imgElement.setAttribute("height", "300");

        var downBtnElement = document.createElement('button');
        downBtnElement.setAttribute("class", "btn fa fa-download");
        downBtnElement.setAttribute("id", "downBtn");
        downBtnElement.setAttribute("name", chat.fileName);
        downBtnElement.setAttribute("onclick", `downloadFile('${chat.fileName}', '${chat.fileDir}')`);


        contentElement.appendChild(imgElement);
        contentElement.appendChild(downBtnElement);

    }else{
        // 만약 s3DataUrl 의 값이 null 이라면
        // 이전에 넘어온 채팅 내용 보여주기기
       var messageText = document.createTextNode(chat.message);
        contentElement.appendChild(messageText);
    }

    messageElement.appendChild(contentElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    console.log("getAvatarColor");
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

messageForm.addEventListener('submit', sendMessage, true)

/// 파일 업로드 부분 ////
function uploadFile(){
    console.log("uploadFile");
    var file = $("#file")[0].files[0];
    var formData = new FormData();
    formData.append("file",file);
    formData.append("roomId", roomId);

    // ajax 로 multipart/form-data 를 넘겨줄 때는
    //         processData: false,
    //         contentType: false
    // 처럼 설정해주어야 한다.

    // 동작 순서
    // post 로 rest 요청한다.
    // 1. 먼저 upload 로 파일 업로드를 요청한다.
    // 2. upload 가 성공적으로 완료되면 data 에 upload 객체를 받고,
    // 이를 이용해 chatMessage 를 작성한다.
    $.ajax({
        type : 'POST',
        url : '/s3/upload',
        data : formData,
        processData: false,
        contentType: false
    }).done(function (data){
        // console.log("업로드 성공")

        var chatMessage = {
            "roomId": roomId,
            sender: username,
            message: username+"님의 파일 업로드",
            type: 'TALK',
            s3DataUrl : data.s3DataUrl, // Dataurl
            "fileName": file.name, // 원본 파일 이름
            "fileDir": data.fileDir // 업로드 된 위치
        };

        // 해당 내용을 발신한다.
        stompClient.send("/pub/chat/sendMessage", {}, JSON.stringify(chatMessage));
    }).fail(function (error){
        alert(error);
    })
}

// 파일 다운로드 부분 //
// 버튼을 누르면 downloadFile 메서드가 실행됨
// 다운로드 url 은 /s3/download+원본파일이름
function downloadFile(name, dir){
    // console.log("파일 이름 : "+name);
    // console.log("파일 경로 : " + dir);
    let url = "/s3/download/"+name;

    // get 으로 rest 요청한다.
    $.ajax({
        url: "/s3/download/"+name, // 요청 url 은 download/{name}
        data: {
            "fileDir" : dir // 파일의 경로를 파라미터로 넣는다.
        },
        dataType: 'binary', // 파일 다운로드를 위해서는 binary 타입으로 받아야한다.
        xhrFields: {
            'responseType': 'blob' // 여기도 마찬가지
        },
        success: function(data) {

            var link = document.createElement('a');
            link.href = URL.createObjectURL(data);
            link.download = name;
            link.click();
        }
    });
}

$("#enterChatRoom").click(function () {
    let inputUserId = document.getElementById("userId");
    let inputUserName = document.getElementById("name");
    if(inputUserId.value.length == 0 || inputUserId.value.trim() == null) { // 입력 값이 없거나 null인 경우
        alert("아이디를 입력하세요.");
        return false;
    } else if (inputUserName.value.length == 0 || inputUserName.value == null) {
        alert("닉네임을 입력하세요.");
        return false;
    }
    usernameForm.addEventListener('submit', connect, true)
});

$("select[name=targetId]").change(function (){
    targetId=$("select[name=targetId] option:selected").val();
});