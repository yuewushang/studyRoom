package UserService.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
        //设置连接
        redisTemplate.setConnectionFactory(redisConnect);
        //创建GenericJackson2JsonRedisSerializer序列化器
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer=new GenericJackson2JsonRedisSerializer();
        //设置字符串型的redis序列化器
        //设置key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //设置value的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        //初始化RedisTemplate，使之序列化完成
        redisTemplate.afterPropertiesSet();


        //返回redisTemplate
        return  redisTemplate;

    }

}
