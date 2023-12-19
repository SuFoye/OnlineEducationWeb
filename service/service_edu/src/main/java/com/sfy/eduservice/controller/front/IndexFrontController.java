package com.sfy.eduservice.controller.front;

import com.sfy.commonutils.R;
import com.sfy.eduservice.entity.EduCourse;
import com.sfy.eduservice.entity.EduTeacher;
import com.sfy.eduservice.service.EduCourseService;
import com.sfy.eduservice.service.EduTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eduservice/indexfront")

public class IndexFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduTeacherService teacherService;

    //根据id查询前8条热门课程，查询前4条名师
    @GetMapping("indexFront")
    public R index() {
        List<EduCourse> courseList = courseService.selectHotCourses();
        List<EduTeacher> teacherList = courseService.selectHotTeachers();
        return R.ok().data("courseList", courseList).data("teacherList", teacherList);
    }
}
