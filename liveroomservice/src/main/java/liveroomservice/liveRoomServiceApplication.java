package liveroomservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
public class liveRoomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(liveRoomServiceApplication.class,args);
        log.info("liveRoomService启动成功！");
    }
}
