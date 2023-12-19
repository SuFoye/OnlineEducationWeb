package com.sfy.eduservice.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sfy.commonutils.R;
import com.sfy.eduservice.entity.EduCourse;
import com.sfy.eduservice.entity.dto.admin.CourseInfo;
import com.sfy.eduservice.entity.dto.admin.CourseQuery;
import com.sfy.eduservice.entity.vo.admin.CoursePublishVo;
import com.sfy.eduservice.service.EduCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
@RestController
@RequestMapping("/eduservice/course")

public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    //查询所有课程数据
    @GetMapping("getCourseList")
    public R getAllCourse() {
        List<EduCourse> courseList = courseService.list(null);
        return R.ok().data("courseList", courseList);
    }

    //分页查询所有课程
    //current：当前页，limit：每页记录数
    @GetMapping("pageCourse/{current}/{limit}")
    public R pageCourseList(@PathVariable long current,
                            @PathVariable long limit) {
        Page<EduCourse> coursePage = new Page<>(current, limit);
        courseService.page(coursePage, null);

        long total = coursePage.getTotal();
        List<EduCourse> records = coursePage.getRecords();

        return R.ok().data("total", total).data("rows", records);
    }

    //条件查询课程带分页
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageCourseCondition(@PathVariable long current,
                                 @PathVariable long limit,
                                 @RequestBody(required = false) CourseQuery courseQuery) {
        Page<EduCourse> coursePage = new Page<>(current, limit);
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();

        String title = courseQuery.getTitle();
        String teacherId = courseQuery.getTeacherId();
        String subjectParentId = courseQuery.getSubjectParentId();
        String subjectId = courseQuery.getSubjectId();
        String status = courseQuery.getStatus();

        if(!StringUtils.isEmpty(title)) {
            wrapper.like("title", title);
        }
        if(!StringUtils.isEmpty(teacherId)) {
            wrapper.eq("teacher_id", teacherId);
        }
        if(!StringUtils.isEmpty(subjectParentId)) {
            wrapper.eq("subject_parent_id", subjectParentId);
        }
        if(!StringUtils.isEmpty(subjectId)) {
            wrapper.eq("subject_id", subjectId);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }

        courseService.page(coursePage, wrapper);
        long total = coursePage.getTotal();
        List<EduCourse> records = coursePage.getRecords();

        return R.ok().data("total", total).data("records", records);
    }

    //添加课程基本信息
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfo courseInfo) {
        //返回添加之后课程id，为了后面添加大纲使用
        String id= courseService.saveCourseInfo(courseInfo);
        return R.ok().data("courseId", id);
    }

    //根据课程id查询课程基本信息
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId) {
        CourseInfo courseInfo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfo", courseInfo);
    }

    //修改课程信息
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfo courseInfo) {
        courseService.updateCourseInfo(courseInfo);
        return R.ok();
    }

    //根据课程id查询课程确认信息
    @GetMapping("getPublishCourseInfo/{courseId}")
    public R getPublishCourseInfo(@PathVariable String courseId) {
        CoursePublishVo coursePublishVo = courseService.publishCourseInfo(courseId);
        return R.ok().data("publishCourseInfo", coursePublishVo);
    }

    //课程最终发布，修改课程状态
    @PostMapping("publishCourse/{courseId}")
    public R publishCourse(@PathVariable String courseId) {
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(courseId);
        eduCourse.setStatus("Normal"); //设置课程发布状态
        courseService.updateById(eduCourse);
        return R.ok();
    }

    //删除课程
    @DeleteMapping("delete/{courseId}")
    public R deleteCourse(@PathVariable String courseId) {
        courseService.removeCourse(courseId);
        return R.ok();
    }

}

