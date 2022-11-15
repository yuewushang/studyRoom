package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.StudyRoomPlan;
import liveroomservice.Mapper.StudyRoomPlanMapper;
import liveroomservice.Service.StudyRoomPlanService;
import org.springframework.stereotype.Service;

//这是一个业务层bean
@Service
public class StudyRoomPlanServiceImpl extends ServiceImpl<StudyRoomPlanMapper, StudyRoomPlan> implements StudyRoomPlanService {
}
