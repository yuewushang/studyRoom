package UserService.Config;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //创建队列
    @Bean
    public Queue addUser(){
        return new Queue("addUser");
    }
}
