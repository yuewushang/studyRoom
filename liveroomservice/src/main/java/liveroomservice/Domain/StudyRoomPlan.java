package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_room_plan")
public class StudyRoomPlan {
    @TableId("study_room_plan_id")
    private Long studyRoomPlanId;//自习室计划的id
    private String studyRoomPlanName;//自习室计划的名称
    private String studyRoomPlanContent;//自习室计划的内容
    private String studyRoomPlanType;//自习室计划的类型，日计划，周计划，月计划，年计划
    private Long studyRoomId;//该计划所属自习室的id
    @TableLogic
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
