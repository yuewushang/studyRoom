package InteractionService.Controller;

import InteractionService.Common.R;
import InteractionService.Domain.Message;
import InteractionService.Service.MessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//webscoket服务器
@Component
@Slf4j
@ServerEndpoint("/interaction/websocket/{userInformation}")
public class WebsocketService {
    //当前在线人数
    public static int onLineNumber;
    //创建一个哈希表来存放每个用户对应的websocketService
    //使用concurentHashMap来解决高并发的读写冲突问题
    public static ConcurrentHashMap<String,WebsocketService> websocketServiceConcurrentHashMap=new ConcurrentHashMap<>();
    public String userInformation;//接收到的直播间房间号+弹幕发送者的姓名
    public Session session;//当前会话
    private static MessageService messageService;
    //因为websocket多对象和spring注入单例冲突，所以写一个方法来注入之
    @Autowired
    public void setServiceBean(MessageService messageService){
        WebsocketService.messageService=messageService;
    }

    /**
     * 当建立连接的时候
     * @param userInformation
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userInformation") String userInformation, Session session){
        this.session=session;
        this.userInformation=userInformation;
        //如果原来没有，则在线人数增加
        if(!websocketServiceConcurrentHashMap.containsKey(userInformation)){
            addOnlineNumber();
        }
        websocketServiceConcurrentHashMap.put(userInformation,this);

        log.info("用户"+userInformation+"连接"+"当前在线人数为"+getOnlineNumber());
    }

    /**
     * 当关闭连接的时候
     * @return
     */
    @OnClose
    public void onClose(){
        if(websocketServiceConcurrentHashMap.containsKey(userInformation)){
            websocketServiceConcurrentHashMap.remove(this);
            websocketServiceConcurrentHashMap.remove(userInformation);
            subOnlineNumber();
        }
        log.info("用户"+userInformation+"退出，当前在线人数为"+getOnlineNumber());
    }

    /**
     * 当接收到消息的时候
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message,Session session) throws IOException {
        log.info("用户"+userInformation+"发送弹幕："+message);
        JSONObject object = JSON.parseObject(message);
        String userName=object.get("userInformation").toString().substring(19);
        String messageToSend=object.getString("messageToSend");
        //获取当前时间
        LocalDateTime time=LocalDateTime.now();
        JSONObject object1=new JSONObject();
        object1.put("userName",userName);
        object1.put("time",time);
        object1.put("message",messageToSend);
        //将数据存储到数据库中
        Message messageToStore=new Message();
        messageToStore.setCreateTime(time);
        messageToStore.setMessageType(object.get("type").toString());
        messageToStore.setMessageContent(messageToSend);
        messageToStore.setRoomId(userInformation.substring(0,19));
        messageToStore.setUserName(userName);
        log.info("存储消息{}",messageToStore);
        messageService.save(messageToStore);
        //向该房间中的所有人发送消息
        Iterator<Map.Entry<String, WebsocketService>> iterator = websocketServiceConcurrentHashMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, WebsocketService> next = iterator.next();
            String key = next.getKey();
            //如果是同一个直播间中的信息
            if(userInformation.substring(0,19).equals(key.substring(0,19))){
                next.getValue().sendMessage(JSON.toJSONString(object1));
            }
        }
    }

    /**
     * 向与某个服务器端websocket连接的客户端发送消息
     * @param message
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 当出错的时候
     * @param session
     * @param error
     * @return
     */
    @OnError
    public void onError(Session session,Throwable error){
        log.error("出错了{}",error.getMessage());
        error.printStackTrace();
    }

    /**
     * 增加在线人数，synchronized解决并发同步问题
     */
    public static synchronized void addOnlineNumber(){
        onLineNumber++;
    }

    /**
     * 减少在线人数，synchronized解决并发同步问题
     */
    public static synchronized void subOnlineNumber(){
        onLineNumber--;
    }

    /**
     * 获取在线人数，synchronized解决并发同步问题
     * @return
     */
    public static synchronized Integer getOnlineNumber(){
        return onLineNumber;
    }


}
