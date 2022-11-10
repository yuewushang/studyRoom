package InteractionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class InteractionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InteractionServiceApplication.class,args);
        log.info("interactionService启动成功！");
    }
}
