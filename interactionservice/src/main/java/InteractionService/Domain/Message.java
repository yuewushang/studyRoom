package InteractionService.Domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

//直播间中的消息的实体类
@Data
@TableName("message")
public class Message {
    @TableId()
    private Long messageId;
    private String roomId;
    private String userName;
    private String messageType;
    private String messageContent;
    private LocalDateTime createTime;
}
