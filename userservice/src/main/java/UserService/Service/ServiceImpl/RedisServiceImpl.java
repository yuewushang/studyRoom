package UserService.Service.ServiceImpl;

import UserService.Common.R;
import UserService.Dto.LikesDto;
import UserService.Service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//服务层bean
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加点赞
     * @param likesDto
     * @return
     */
    @Override
    public R<String> addLikes(LikesDto likesDto) {
        //封装key值
        String key=likesDto.getArticleId()+":"+likesDto.getUserId();
        //获取操作哈希的对象
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.put("article",key,likesDto.getUserName());
        log.info("存储用户"+likesDto.getUserId()+"对动态ID为"+likesDto.getArticleId()+" 的点赞信息");
        return R.success("添加点赞信息成功");
    }

    /**
     * 删除某个点赞信息
     * @param likesDto
     * @return
     */
    @Override
    public R<String> deleteLikes(LikesDto likesDto){
        //封装key值
        String key=likesDto.getArticleId()+":"+likesDto.getUserId();
        //获取操作哈希的对象
        HashOperations hashOperations = redisTemplate.opsForHash();
        //删除点赞信息
        hashOperations.delete("article",key);
        log.info("删除用户"+likesDto.getUserId()+"对动态ID为"+likesDto.getArticleId()+" 的点赞信息");
        return R.success("删除点赞信息成功");
    }

    /**
     * 获取所有的点赞信息
     * @return
     */
    @Override
    public R<List<String>> getAllLikes() {
        //获取操作哈希的对象
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map article = hashOperations.entries("article");
        log.info(article.toString());
        ArrayList<String>list=new ArrayList<>();
        //遍历集合，将其值转换为数组的形式
        for(Object key:article.keySet()){
            Object o = article.get(key);
            String keyString= (String) key;
            String result=keyString+"="+o;
            list.add(result);
        }
        System.out.println(list);
        return R.success(list);
    }
}
