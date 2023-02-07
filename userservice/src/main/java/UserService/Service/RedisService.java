package UserService.Service;

import UserService.Common.R;
import UserService.Dto.LikesDto;

import java.util.List;
import java.util.Map;

public interface RedisService {
    public R<String> addLikes(LikesDto likesDto);
    public R<String> deleteLikes(LikesDto likesDto);
    public R<List<String>> getAllLikes();
}
