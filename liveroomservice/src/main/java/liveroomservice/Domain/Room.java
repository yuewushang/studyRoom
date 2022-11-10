package liveroomservice.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
//直播间实体类
@TableName("room")
public class Room {
    @TableId("room_id")
    Long roomId;
    String roomName;
    String roomContent;//直播间内容简介
    String roomType;//该直播间的类型
    Long masterId;//直播间的创建者的id
    LocalDateTime startTime;//直播开始时间
    String status;//直播间的状态
    String roomImage;//直播间的封面
    Integer roomPeopleNumber;//直播间人数
    Integer roomTemperature;//直播间人气

    //插入时更新
    @TableField(fill = FieldFill.INSERT)
    LocalDateTime createTime;
    //插入和修改时更新
    @TableField(fill = FieldFill.INSERT_UPDATE)
    LocalDateTime updateTime;
    //逻辑删除字段
    @TableLogic
    Integer isDelete;
}
