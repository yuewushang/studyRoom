package UserService.Dto;

import UserService.Domain.UserFriendMessage;
import lombok.Data;

//用于添加好友时，列表的双向操作
@Data
public class UserFriendMessageDto extends UserFriendMessage {
    private String friendName;
}
