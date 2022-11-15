package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.StudyRoom;
import liveroomservice.Mapper.StudyRoomMapper;
import liveroomservice.Service.StudyRoomService;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class StudyRoomServiceImpl extends ServiceImpl<StudyRoomMapper, StudyRoom> implements StudyRoomService {
}
