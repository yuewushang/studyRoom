package searchService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import searchService.Common.R;
import searchService.Domain.StudyRoom;

@FeignClient("liveroomservice")
public interface StudyRoomClient {
    @GetMapping("/studyRoom/getStudyRoomMessage")
    public R<StudyRoom>  getStudyRoomMessage(@RequestParam("studyRoomId") String studyRoomId);
}
