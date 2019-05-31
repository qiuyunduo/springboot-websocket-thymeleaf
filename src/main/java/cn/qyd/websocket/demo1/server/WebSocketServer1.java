package cn.qyd.websocket.demo1.server;

import cn.qyd.websocket.demo1.model.WebSocketMsg;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author qyd
 * @Date 19-5-29 下午6:10
 **/

@ServerEndpoint(value = "/websocket/wechat/{nickname}")
@Component
@Slf4j
public class WebSocketServer1 {
    private static int onLineCount = 0;

    private static CopyOnWriteArrayList<WebSocketServer1> webSocketServers = new CopyOnWriteArrayList<>();

    private static HashMap<String, Session> map = new HashMap<>();

    private Session session;

    private String nickName;

    /**
     * websocket连接成功调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("nickname") String nickname) {
        this.session = session;
        this.nickName = nickname;
        map.put(session.getId(), session);
        webSocketServers.add(this);
        addOnLineCount();
        log.info("新用户'"+nickname+"'连接进入系统：当前连接数为： "+getOnLineCount());
        sendInfo("系统消息：　欢迎”"+nickname+"“加入无聊聊天室 (频道号为： "+session.getId()+")");
    }

    @OnClose
    public void onClose() {
        webSocketServers.remove(this);
        subOnLineCount();
        sendInfo("用户“"+this.nickName+"“离开聊天室");
        log.info("用户“"+this.nickName+"离开聊天室”，当前人数为："+getOnLineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("来自用户'"+this.nickName+"'的消息："+message);
        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketMsg webSocketMsg;
        try {
            webSocketMsg = objectMapper.readValue(message, WebSocketMsg.class);
            String msg = this.nickName+"　：　"+webSocketMsg.getMessage();
            if("0".equals(webSocketMsg.getType())) {
                sendInfo(msg); //群聊，群发消息
            } else if("1".equals(webSocketMsg.getType())) {
                Session toSession = map.get(webSocketMsg.getToUser());
                if(toSession != null) {
                    msg += " (悄悄话)";
                    this.session.getAsyncRemote().sendText(msg); //后台系统发送给发送方
                    toSession.getAsyncRemote().sendText(msg);  //后台发送给接受者
                } else {
                    this.session.getAsyncRemote().sendText("系统消息: 对方不在线或频道号错误");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时候调用的方法
     */
    @OnError
    public void onError(Throwable throwable) {
        log.error("用户“"+this.nickName+"”连接发生错误");
        throwable.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public void sendInfo(String message) {
        log.info("群发消息："+message);
        for(WebSocketServer1 socketServer : webSocketServers) {
            socketServer.session.getAsyncRemote().sendText(message);
        }
    }

    public static synchronized int getOnLineCount() {
        return onLineCount;
    }

    public static synchronized void addOnLineCount() {
        WebSocketServer1.onLineCount++;
    }

    public static synchronized void subOnLineCount() {
        WebSocketServer1.onLineCount--;
    }
}
