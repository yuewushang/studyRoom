package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//直播间观众实体类
@TableName("audience")
public class Audience {
    Long userId;
    Long roomId;
    String audienceName;//观众在直播间中的名字
    String audienceIdentify;//观众的身份
    String audienceImage;//观众的头像
    String audienceStatus;//观众的状态，禁言与否
    String audienceScore;//观众的活跃度
    LocalDateTime enterTime;
    LocalDateTime leaveTime;
    @TableLogic
    Integer isDelete;//逻辑删除
}
