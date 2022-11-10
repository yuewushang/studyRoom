package storageservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import storageservice.Domain.Image;
import storageservice.Mapper.ImageMapper;
import storageservice.Service.ImageService;

//这是一个业务层bean
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {
}
