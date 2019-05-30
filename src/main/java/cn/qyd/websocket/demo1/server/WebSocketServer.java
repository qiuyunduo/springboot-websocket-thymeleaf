package cn.qyd.websocket.demo1.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author qyd
 * @Date 19-5-29 下午6:10
 **/

@ServerEndpoint(value = "/websocket")
@Component
@Slf4j
public class WebSocketServer {
    private static int onLineCount = 0;

    private static CopyOnWriteArrayList<WebSocketServer> webSocketServers = new CopyOnWriteArrayList<>();

    private Session session;

    /**
     * websocket连接成功调用的方法
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketServers.add(this);
        addOnLineCount();
        log.info("有新的连接接入！ 当前连接数为： "+onLineCount);
        try {
            sendMessage("链接成功，Hello world!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     */
    public void sendInfo(String message) {
        log.info("群发消息："+message);
        for(WebSocketServer socketServer : webSocketServers) {
            try {
                socketServer.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnLineCount() {
        return onLineCount;
    }

    public static synchronized void addOnLineCount() {
        WebSocketServer.onLineCount++;
    }

    public static synchronized void subOnLineCount() {
        WebSocketServer.onLineCount--;
    }
}
