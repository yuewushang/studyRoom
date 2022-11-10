package storageservice.Domain;

import lombok.Data;

//自动补全set和get方法
@Data
public class Image {
    String imageName;
    byte[] imageData;
}
