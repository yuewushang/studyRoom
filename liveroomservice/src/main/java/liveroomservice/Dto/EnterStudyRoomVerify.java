package liveroomservice.Dto;

import lombok.Data;

@Data
public class EnterStudyRoomVerify {
        private Long studyRoomId;
        private Long userId;
        private String userName;
        private String studyRoomPassword;
}
