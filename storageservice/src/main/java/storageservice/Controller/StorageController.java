package storageservice.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import storageservice.Common.R;
import storageservice.Domain.Image;
import storageservice.Service.ImageService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/storage")
public class StorageController {
    @Autowired
    private ImageService imageService;
    //上传图片
    @PostMapping("/image/upload")
    public R<String> uploadImage(MultipartFile file) throws IOException {
        //获取原始文件的后缀名
        String houzhui=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        //使用UUID生成唯一id
        String imageName= UUID.randomUUID().toString()+houzhui;
        //将图片存储到数据库中
        Image image=new Image();
        image.setImageName(imageName);
        image.setImageData(file.getBytes());
        boolean save = imageService.save(image);
        //返回提示信息
        return R.success(imageName);
    }

    /**
     * 获取数据库中的图片
     * @param imageUrl
     */
    @GetMapping("/image/getImage")
    public R<String> getImage(String imageUrl,HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Image>wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(imageUrl!=null,Image::getImageName,imageUrl);
        Image one = imageService.getOne(wrapper);
        //返回图片的字节数据
        //获取输出流
        OutputStream outputStream=response.getOutputStream();
        outputStream.write(one.getImageData());
        //关闭输出流
        outputStream.close();
        return R.success("获取图片成功");
    }
}
