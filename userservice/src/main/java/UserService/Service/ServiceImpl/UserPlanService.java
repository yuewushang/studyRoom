package UserService.Service.ServiceImpl;

import UserService.Domain.UserPlan;
import UserService.Mapper.UserPlanMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class UserPlanService extends ServiceImpl<UserPlanMapper, UserPlan> implements UserService.Service.UserPlanService {
}
