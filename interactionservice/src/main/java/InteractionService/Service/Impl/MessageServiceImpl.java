package InteractionService.Service.Impl;

import InteractionService.Domain.Message;
import InteractionService.Mapper.MessageMapper;
import InteractionService.Service.MessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
}
