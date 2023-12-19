package com.sfy.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sfy.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sfy.eduservice.entity.EduTeacher;
import com.sfy.eduservice.entity.dto.admin.CourseInfo;
import com.sfy.eduservice.entity.dto.front.CourseFront;
import com.sfy.eduservice.entity.vo.admin.CoursePublishVo;
import com.sfy.eduservice.entity.vo.front.CourseFrontVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
public interface EduCourseService extends IService<EduCourse> {

    //添加课程基本信息的方法
    String saveCourseInfo(CourseInfo courseInfo);

    //根据课程id查询课程基本信息
    CourseInfo getCourseInfo(String courseId);

    //修改课程信息
    void updateCourseInfo(CourseInfo courseInfo);

    //根据课程id查询课程确认信息
    CoursePublishVo publishCourseInfo(String courseId);

    //删除课程
    void removeCourse(String courseId);

    //根据id查询前8条热门课程（前台部分）
    List<EduCourse> selectHotCourses();

    //根据id查询前4条名师（前台部分）
    List<EduTeacher> selectHotTeachers();

    //条件查询带分页查询（前台部分）
    Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFront courseFront);

    //根据课程id，编写sql语句查询课程信息
    CourseFrontVo getBaseCourseInfo(String courseId);
}
