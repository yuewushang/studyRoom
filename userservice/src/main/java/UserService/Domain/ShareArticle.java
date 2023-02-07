package UserService.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("share_article")
public class ShareArticle {
    @TableId("article_id")
    private Long articleId;
    private Long userId;
    private String userName;
    private String userImageUrl;
    private String content;
    private String articleImageUrl;
    private String scope;
    @TableLogic
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
