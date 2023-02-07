package UserService.Controller;

import UserService.Common.R;
import UserService.Domain.UserFriendMessage;
import UserService.Domain.UserFriends;
import UserService.Dto.UserFriendMessageDto;
import UserService.Service.UserFriendMessageService;
import UserService.Service.UserFriendsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Restful风格的控制层bean
@RestController
@RequestMapping("/user/userFriend")
public class UserFriendController {

    @Autowired
    private UserFriendMessageService userFriendMessageService;

    @Autowired
    private UserFriendsService userFriendsService;


    /**
     * 发送添加朋友的请求信息
     * @param userFriendMessage
     * @return
     */
    @PostMapping("/sendAddFriendMessage")
    //事务一致
    @Transactional
    public R<String> sendAddFriendMessage(@RequestBody UserFriendMessage userFriendMessage){
        //如果是用户自己加自己
        if(userFriendMessage.getUserId().equals(userFriendMessage.getFriendId())){
            return R.error("自己怎么可以和自己做朋友呢，哈哈哈");
        }
        //查询请求列表中是否已经存在
        LambdaQueryWrapper<UserFriendMessage>messageWrapper=new LambdaQueryWrapper<>();
        messageWrapper.eq(UserFriendMessage::getFriendId,userFriendMessage.getFriendId());
        UserFriendMessage one = userFriendMessageService.getOne(messageWrapper);
        if(one!=null){
            return R.error("请求已经发出，请勿重复操作");
        }
        //获取该用户的好友列表
        //构造查询条件
        LambdaQueryWrapper<UserFriends>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserFriends::getUserId,userFriendMessage.getUserId());
        List<UserFriends> myFriendList = userFriendsService.list(wrapper);
        //检测列表中是否已经有该好友
        for(int i=0;i<myFriendList.size();i++){
            UserFriends userFriends = myFriendList.get(i);
            if(userFriends.getFriendId().equals(userFriendMessage.getFriendId())){
                return R.error("请勿重复添加好友");
            }
        }
        //如果一切正常，添加添加好友的信息
        userFriendMessageService.save(userFriendMessage);
        return R.success("发送好友请求成功，请等待对方回应~");
    }

    /**
     * 获取想要添加该用户的所有未处理请求信息
     * @param userId
     * @return
     */
    @GetMapping("/getFriendsRequestMessage")
    public R<List<UserFriendMessage>> getFriendsRequestMessage(String userId){
        //构造查询条件
        LambdaQueryWrapper<UserFriendMessage>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserFriendMessage::getFriendId,userId);
        wrapper.eq(UserFriendMessage::getState,"未读");
        wrapper.orderByAsc(UserFriendMessage::getCreateTime);
        List<UserFriendMessage> list = userFriendMessageService.list(wrapper);
        return R.success(list);
    }


    /**
     * 用户处理好友请求信息
     * @param userFriendMessage
     * @return
     */
    @PostMapping("/handleFriendRequest")
    @Transactional
    public R<String> handleFriendRequest(@RequestBody UserFriendMessageDto userFriendMessage){
        //修改好友请求消息状态
        UserFriendMessage byId = userFriendMessageService.getById(userFriendMessage.getMessageId());
        byId.setState(userFriendMessage.getState());
        userFriendMessageService.updateById(byId);
        //如果同意好友请求
        if(userFriendMessage.getState().equals("同意")){
            UserFriends userFriends=new UserFriends();
            userFriends.setUserId(userFriendMessage.getUserId());
            userFriends.setFriendId(userFriendMessage.getFriendId());
            userFriends.setFriendName(userFriendMessage.getUserName());
            //保存到数据库
            userFriendsService.save(userFriends);
            //好友的那一边也要弄一个
            UserFriends userFriends1=new UserFriends();
            userFriends1.setUserId(userFriendMessage.getFriendId());
            userFriends1.setFriendId(userFriendMessage.getUserId());
            userFriends1.setFriendName(userFriendMessage.getFriendName());
            userFriendsService.save(userFriends1);
        }
        return R.success("操作成功");
    }

    /**
     * 查询好友列表
     * @param userId
     */
    @GetMapping("/getMyFriendsList")
    public R<List<UserFriends>> getMyFriendsList(String userId){
        //构造查询条件
        LambdaQueryWrapper<UserFriends>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserFriends::getUserId,Long.parseLong(userId));
        List<UserFriends> list = userFriendsService.list(wrapper);
        return R.success(list);
    }


}
