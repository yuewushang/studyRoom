package storageservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StorageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class,args);
        log.info("storageService启动成功");
    }
}
