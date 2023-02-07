package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_plan")
public class StudyRoomPlan {
    @TableId("user_plan_id")
    private Long userPlanId;
    private String planContent;
    private Long userId;
    private Long studyRoomId;
    private LocalDateTime createPlanTime;
    @TableLogic
    private Integer isDelete;
}
