package UserService.Dto;

import UserService.Domain.User;
import lombok.Data;

@Data
public class RegisterDto extends User {
    private String identifyCode;//验证码

    @Override
    public String toString() {
        return  "RegisterDto{" +
                "identifyCode='" + identifyCode + '\'' +
                '}';
    }
}
