package liveroomservice.Dto;

import lombok.Data;

@Data
public class ShareStudyRoom {
    private String studyRoomName;
    private String studyRoomIntroduction;//自习室的简介
    private String studyRoomPassword;//自习室的密码
}
