package UserService.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//重新设置redis的序列化方式
@Configuration
public class RedisConfig extends CachingConfigurerSupport {


    @Bean
    //新建一个新的redisTemplate
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnect){
        //创建一个redistemplate对象
        RedisTemplate<Object,Object>redisTemplate=new RedisTemplate<>();
        //设置字符串型的redis序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        //设置连接
        redisTemplate.setConnectionFactory(redisConnect);
        //返回redisTemplate
        return  redisTemplate;

    }

}
