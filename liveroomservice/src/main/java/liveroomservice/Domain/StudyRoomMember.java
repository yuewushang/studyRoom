package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_room_member")
public class StudyRoomMember {
    private Long studyRoomId;//自习室的id
    private Long userId;//用户的id
    private String userName;//用户名
    private Integer isOnline;//是否在线，1为在线，0为离线，默认为1
    private Double lengthOfStudy;//学习时长，以小时计算
    private String memberIdentify;//成员的身份
    @TableLogic
    private Integer isDelete;//逻辑删除字段
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//该成员加入该自习室的时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//更新时间
}
