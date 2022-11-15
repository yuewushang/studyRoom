package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.StudyRoomMember;
import liveroomservice.Mapper.StudyRoomMapper;
import liveroomservice.Mapper.StudyRoomMemberMapper;
import liveroomservice.Service.StudyRoomMemberService;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class StudyRoomMemberServiceImpl extends ServiceImpl<StudyRoomMemberMapper, StudyRoomMember> implements StudyRoomMemberService {
}
