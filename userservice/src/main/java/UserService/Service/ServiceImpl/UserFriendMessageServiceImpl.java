package UserService.Service.ServiceImpl;

import UserService.Domain.UserFriendMessage;
import UserService.Mapper.UserFriendMessageMapper;
import UserService.Service.UserFriendMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class UserFriendMessageServiceImpl extends ServiceImpl<UserFriendMessageMapper, UserFriendMessage> implements UserFriendMessageService {
}
