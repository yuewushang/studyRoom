package UserService.Domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_comment")
public class ArticleComment {
    @TableId("comment_id")
    private Long commentId;
    private Long articleId;
    private Long userId;
    private String userName;
    private String userImageUrl;
    private String replyTo;
    private String commentText;
    @TableLogic
    private Integer isDelete;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
