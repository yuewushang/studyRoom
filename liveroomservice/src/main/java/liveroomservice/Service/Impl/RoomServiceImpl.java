package liveroomservice.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liveroomservice.Domain.Room;
import liveroomservice.Mapper.RoomMapper;
import liveroomservice.Service.RoomService;
import org.springframework.stereotype.Service;

//业务层bean
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {
}
