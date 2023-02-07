package UserService.Service.ServiceImpl;

import UserService.Domain.ShareArticle;
import UserService.Mapper.ShareArticleMapper;
import UserService.Service.ShareArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class ShareArticleServiceImpl extends ServiceImpl<ShareArticleMapper, ShareArticle> implements ShareArticleService {
}
