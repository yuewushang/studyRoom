package UserService.Domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
//实现序列化，好放入redis缓存中
public class User implements Serializable {
    @TableId("user_id")
    private Long userId;
    private String userName;
    private String password;
    private String streamWord;//串流密钥
    private Integer state;//用户状态 0 ：未登录  1：已登录
    private String status;//用户的身份
    private String imageUrl;//用户的头像的名称
    private Boolean checked;//是否记住账号密码
    //以下为自动填充字段
    //插入时填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //插入时填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;//创建者的id
    //插入合更新都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //插入合更新都填充
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;//修改人的id
}
