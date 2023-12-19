package com.sfy.eduorder.client;

import com.sfy.commonutils.ordervo.CourseFrontVoOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("service-edu")
public interface EduClient {

    //根据课程id查询课程信息，新建一个公共类CourseFrontVoOrder封装信息
    @PostMapping("/eduservice/coursefront/getCourseInforOrder/{courseId}")
    public CourseFrontVoOrder getCourseInfoOrder(@PathVariable("courseId") String courseId);
}
