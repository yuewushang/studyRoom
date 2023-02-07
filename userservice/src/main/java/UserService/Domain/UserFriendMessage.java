package UserService.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_friend_message")
public class UserFriendMessage {
    @TableId("message_id")
    private Long messageId;
    private Long userId;
    private String userName;
    private Long friendId;
    private String state;
    private String content;
    @TableLogic
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
