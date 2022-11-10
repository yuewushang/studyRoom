package storageservice.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import storageservice.Domain.Image;

//这是一个mapper
@Mapper
public interface ImageMapper extends BaseMapper<Image> {
}
