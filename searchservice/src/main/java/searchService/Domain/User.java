package searchService.Domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
    private List<String> suggestion;//自动补全字段



}
