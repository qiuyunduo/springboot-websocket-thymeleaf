package cn.qyd.websocket.demo1.model;

import lombok.Data;

/**
 * @Author qyd
 * @Date 19-5-30 下午6:06
 **/
@Data
public class WebSocketMsg {
    private String type;
    private String toUser; //接受者
    private String message; //发送的消息
}
