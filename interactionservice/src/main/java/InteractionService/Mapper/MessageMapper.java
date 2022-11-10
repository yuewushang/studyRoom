package InteractionService.Mapper;

import InteractionService.Domain.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//这是一个mapper
@Mapper
//数据层bean
@Repository
public interface MessageMapper extends BaseMapper<Message> {
}
