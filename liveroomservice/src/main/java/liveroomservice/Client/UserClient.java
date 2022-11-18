package liveroomservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("userservice")
public interface UserClient {
    //一个post请求
    @PostMapping("/user/getFourDeskMate")
    public List<String> getFourDeskMate(List<Long> userList);
}
