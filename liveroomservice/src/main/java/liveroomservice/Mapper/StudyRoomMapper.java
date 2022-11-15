package liveroomservice.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liveroomservice.Domain.StudyRoom;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

//这是一个mapper
@Mapper
//数据层bean
@Repository
public interface StudyRoomMapper extends BaseMapper<StudyRoom> {
}
