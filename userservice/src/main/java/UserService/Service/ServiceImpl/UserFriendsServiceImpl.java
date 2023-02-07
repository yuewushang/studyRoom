package UserService.Service.ServiceImpl;

import UserService.Domain.UserFriends;
import UserService.Mapper.UserFriendsMapper;
import UserService.Service.UserFriendsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//服务层bean
@Service
public class UserFriendsServiceImpl extends ServiceImpl<UserFriendsMapper, UserFriends> implements UserFriendsService {
}
