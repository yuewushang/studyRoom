package UserService.Service.ServiceImpl;

import UserService.Domain.QianDao;
import UserService.Mapper.QianDaoMapper;
import UserService.Service.QianDaoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class QianDaoServiceImpl extends ServiceImpl<QianDaoMapper, QianDao> implements QianDaoService {
}
