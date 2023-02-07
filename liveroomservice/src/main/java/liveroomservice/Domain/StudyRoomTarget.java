package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("study_room_target")
public class StudyRoomTarget {
    @TableId("target_id")
    private Long targetId;
    private String title;
    private LocalDateTime deadline;
    private Long studyRoomId;
    private Long userId;
}
