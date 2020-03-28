$(function () {

});

var websocket = null;

function connectWebsocket() {
    var nickname = document.getElementById("nickName").value;

    if(nickname==="") {
        alert("请输入昵称");
        return;
    }

    if('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8080/websocket/wechat/"+nickname);
        $('#myModal').modal('hide')
    } else {
        alert("您的浏览器不支持websocket");
    }

        //连接发生错误是回调方法
    websocket.onerror = function () {
        setMessageInnerHTML('error');
    };

    //连接成功回调方法
    websocket.onopen = function () {
        setMessageInnerHTML('Loc MSG: 成功加入聊天室');
    };

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data);
    };

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML('Loc MSG: 关闭连接，退出聊天室');
    };

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接,防止连接还没有断开就关闭窗口,server端会报错
    window.onbeforeunload = function () {
        websocket.close();
    }
}

//插入消息到页面中
function setMessageInnerHTML(innerHTML) {
    document.getElementById('message').innerHTML += innerHTML + '<br>'
}


//关闭连接
function closeWebscoket() {
    websocket.close();
}

//发送消息
function sendMessage() {
    var message = $('#text').val();
    var reveiveId = $('#receiveId').val();

    var webSocketMsg = { message: message, toUser: reveiveId };
    if(reveiveId == ''){
        //群聊.
        webSocketMsg.type = 0;
    }else{
        //单聊.
        webSocketMsg.type = 1;
    }

    websocket.send(JSON.stringify(webSocketMsg));
    $('#text').val("");
    $('#receiveId').val("");
    // document.getElementById('text').value = "";
}

function test1() {
    alert('测试ｔｅｓｔ1')
}