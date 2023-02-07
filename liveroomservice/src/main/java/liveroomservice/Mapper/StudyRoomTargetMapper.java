package liveroomservice.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liveroomservice.Domain.StudyRoomTarget;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//这是一个数据层bean
@Repository
//这是一个mapper
@Mapper
public interface StudyRoomTargetMapper extends BaseMapper<StudyRoomTarget> {
}
