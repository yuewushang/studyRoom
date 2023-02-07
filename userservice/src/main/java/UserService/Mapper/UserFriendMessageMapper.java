package UserService.Mapper;

import UserService.Domain.UserFriendMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//这是一个mapper
@Mapper
//数据层bean
@Repository
public interface UserFriendMessageMapper extends BaseMapper<UserFriendMessage> {
}
