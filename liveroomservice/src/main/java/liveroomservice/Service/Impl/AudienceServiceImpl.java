package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.Audience;
import liveroomservice.Mapper.AudienceMapper;
import liveroomservice.Service.AudienceService;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class AudienceServiceImpl extends ServiceImpl<AudienceMapper, Audience> implements AudienceService {
}
