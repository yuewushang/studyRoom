package UserService.Service.ServiceImpl;

import UserService.Domain.User;
import UserService.Mapper.UserMapper;
import UserService.Service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
//一个业务层的bean
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
