package UserService.Mapper;

import UserService.Domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.Mapping;

//这是一个mapping
@Mapper
//一个数据层的bean
@Repository
public interface UserMapper extends BaseMapper<User> {
}
