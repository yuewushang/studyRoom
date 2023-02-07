package UserService.Dto;

import lombok.Data;

@Data
public class LikesDto {
    private String articleId;
    private String userId;
    private String userName;
}
