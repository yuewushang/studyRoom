package searchService.Client;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import searchService.Domain.User;

//绑定要调用的服务
@FeignClient("userservice")
public interface UserClient {
    @GetMapping("/user/getUserMessageById")
    public User getUserMessageById(@RequestParam("userId") String userId);
}
