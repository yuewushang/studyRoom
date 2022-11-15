package liveroomservice.Config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//乐观锁的配置
@Configuration
public class MybatisPlusOptLockerConfig {
    @Bean
    public MybatisPlusInterceptor optimisticLockerInnerInterceptor(){
        //设置一个mybatisplus拦截器
        MybatisPlusInterceptor interceptor=new MybatisPlusInterceptor();
        //添加乐观锁子拦截器
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
