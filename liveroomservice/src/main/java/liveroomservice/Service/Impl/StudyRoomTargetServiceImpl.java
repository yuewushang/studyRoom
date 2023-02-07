package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.StudyRoomTarget;
import liveroomservice.Mapper.StudyRoomMemberMapper;
import liveroomservice.Mapper.StudyRoomTargetMapper;
import liveroomservice.Service.StudyRoomTargetService;
import org.springframework.stereotype.Service;

//这是一个业务层bean
@Service
public class StudyRoomTargetServiceImpl extends ServiceImpl<StudyRoomTargetMapper, StudyRoomTarget> implements StudyRoomTargetService {
}
