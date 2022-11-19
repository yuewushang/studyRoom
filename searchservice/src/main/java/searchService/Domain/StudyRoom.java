package searchService.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * studyRoom数据库表映射的实体类
 */
@Data
@TableName("study_room")
public class StudyRoom {
    @TableId("study_room_id")
    private Long studyRoomId;//自习室的id
    private String studyRoomName;//自习室的名字
    private String studyRoomType;//自习室的类型
    private String studyRoomIntroduction;//自习室的简介
    private Integer studyRoomMemberNumber;//自习室成员数
    //使用乐观锁
    @Version
    private Integer version;
    private String studyRoomImageName;//自习室封面图片的名称，用于去查找存储在数据库中的图片
    private String studyRoomPassword;//自习室的密码
    private Long userId;//自习室创建者的id
    private String userName;//自习室创建者的名称
    //自动补全字段
    private List<String> suggestion;
    //逻辑删除字段
    @TableLogic
    private Integer isDelete;
}
