package liveroomservice.Domain;

import lombok.Data;

//srs服务器的存储完一个flv文件后的回调数据的实体类
@Data
public class CallbackDataOnDvr {
    String action;
    String client_id;
    String ip;
    String vhost;
    String app;
    String live;
    String stream;
    String cwd;
    String file;
}
