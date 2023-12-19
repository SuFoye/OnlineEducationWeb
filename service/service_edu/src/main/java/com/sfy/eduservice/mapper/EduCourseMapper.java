package com.sfy.eduservice.mapper;

import com.sfy.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sfy.eduservice.entity.vo.admin.CoursePublishVo;
import com.sfy.eduservice.entity.vo.front.CourseFrontVo;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    public CoursePublishVo getPublishCourseInfo(String courseId);

    //根据课程id，编写sql语句查询课程信息
    CourseFrontVo getBaseCourseInfo(String courseId);
}
