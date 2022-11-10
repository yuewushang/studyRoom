package liveroomservice.Dto;

import liveroomservice.Common.R;
import lombok.Data;

@Data
public class RoomResult {
    String message;
    Integer code;
    Long roomId;//房间的id
}
