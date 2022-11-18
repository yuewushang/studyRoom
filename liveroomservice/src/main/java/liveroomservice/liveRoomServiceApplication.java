package liveroomservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
//启用feign调用其他服务的功能
@EnableFeignClients
public class liveRoomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(liveRoomServiceApplication.class,args);
        log.info("liveRoomService启动成功！");
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
