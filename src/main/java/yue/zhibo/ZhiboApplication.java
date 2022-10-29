package yue.zhibo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ZhiboApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhiboApplication.class, args);
        log.info("直播+父工程启动成功！");
    }

}
