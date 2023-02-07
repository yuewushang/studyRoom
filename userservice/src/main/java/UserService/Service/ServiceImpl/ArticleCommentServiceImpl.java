package UserService.Service.ServiceImpl;

import UserService.Domain.ArticleComment;
import UserService.Mapper.ArticleCommentMapper;
import UserService.Service.ArticleCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class ArticleCommentServiceImpl extends ServiceImpl<ArticleCommentMapper, ArticleComment> implements ArticleCommentService {
}
