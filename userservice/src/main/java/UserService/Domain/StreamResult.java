package UserService.Domain;

import lombok.Data;

@Data
//推拉流地址返回结果
public class StreamResult {
    String pushUrl;//推流地址
    String key;//密钥
}
