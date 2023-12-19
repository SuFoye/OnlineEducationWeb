package com.sfy.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sfy.eduservice.entity.EduCourse;
import com.sfy.eduservice.entity.EduCourseDescription;
import com.sfy.eduservice.entity.EduTeacher;
import com.sfy.eduservice.entity.dto.admin.CourseInfo;
import com.sfy.eduservice.entity.dto.front.CourseFront;
import com.sfy.eduservice.entity.vo.admin.CoursePublishVo;
import com.sfy.eduservice.entity.vo.front.CourseFrontVo;
import com.sfy.eduservice.mapper.EduCourseMapper;
import com.sfy.eduservice.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    //课程描述、小节、章节service注入
    @Autowired
    private EduCourseDescriptionService courseDescriptionService;

    @Autowired
    private EduVideoService videoService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private EduTeacherService teacherService;



    //添加课程基本信息的方法
    @Override
    public String saveCourseInfo(CourseInfo courseInfo) {
        //向课程表添加课程基本信息
        //把CourseInfoVo转换成EduCourse对象
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfo, eduCourse);
        int insert = baseMapper.insert(eduCourse);
/*        if(insert == 0) {
            //添加失败
            throw new HuitongException(20001, "添加课程信息失败");
        }*/

        //获取添加之后课程id
        String cid = eduCourse.getId();
        //向课程简介表添加课程简介
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(cid);
        courseDescription.setDescription(courseInfo.getDescription());
        courseDescriptionService.save(courseDescription);

        return cid;
    }

    //根据课程id查询课程基本信息
    @Override
    public CourseInfo getCourseInfo(String courseId) {

        CourseInfo courseInfo = new CourseInfo();
        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        BeanUtils.copyProperties(eduCourse, courseInfo);
        //查询课程描述表
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        courseInfo.setDescription(courseDescription.getDescription());

        return courseInfo;
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfo courseInfo) {
        //修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfo, eduCourse);
        int ret = baseMapper.updateById(eduCourse);
/*        if(ret == 0) {
            throw new HuitongException(20001, "修改课程信息失败");
        }*/

        //修改课程描述表
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseInfo.getId());
        courseDescription.setDescription(courseInfo.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String courseId) {
        //调用mapper
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(courseId);
        return publishCourseInfo;
    }

    //删除课程
    @Override
    public void removeCourse(String courseId) {
        //1 根据课程id删除小节
        videoService.removeVideoByCourseId(courseId);
        //2 根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);
        //3 根据课程id删除课程描述
        courseDescriptionService.removeById(courseId);
        //4 根据课程id删除课程本身
        baseMapper.deleteById(courseId);
    }

    //根据id查询前8条热门课程
    @Cacheable(key = "'selectCourseList'", value = "course")
    @Override
    public List<EduCourse> selectHotCourses() {
        //查询前8条热门课程
        QueryWrapper<EduCourse> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.orderByDesc("id");
        courseQueryWrapper.last("limit 10");
        List<EduCourse> courseList = baseMapper.selectList(courseQueryWrapper);

        return courseList;
    }

    //根据id查询前4条名师
    @Cacheable(key = "'selectTeacherList'", value = "teacher")
    @Override
    public List<EduTeacher> selectHotTeachers() {
        //查询前4条名师
        QueryWrapper<EduTeacher> teacherQueryWrapper = new QueryWrapper<>();
        teacherQueryWrapper.orderByDesc("id");
        teacherQueryWrapper.last("limit 4");
        List<EduTeacher> teacherList =  teacherService.list(teacherQueryWrapper);
        return teacherList;
    }

    //条件查询带分页查询（前台部分）
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFront courseFront) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();

        //判断条件值是否为空
        if(!StringUtils.isEmpty(courseFront.getSubjectParentId())) { //一级分类
            wrapper.eq("subject_parent_id", courseFront.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFront.getSubjectId())) { //二级分类
            wrapper.eq("subject_id", courseFront.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFront.getBuyCountSort())) { //销量排序
            wrapper.orderByDesc("buy_count");
        }
        if(!StringUtils.isEmpty(courseFront.getGmtCreateSort())) { //最新时间排序
            wrapper.orderByDesc("gmt_create");
        }
        if(!StringUtils.isEmpty(courseFront.getPriceSort())) { //价格排序
            wrapper.orderByDesc("price");
        }

        baseMapper.selectPage(pageCourse, wrapper);

        //把分页数据获取出来，放到map
        List<EduCourse> records = pageCourse.getRecords();
        long total = pageCourse.getTotal();
        long current = pageCourse.getCurrent();
        long size = pageCourse.getSize();
        long pages = pageCourse.getPages();
        boolean hasNext = pageCourse.hasNext();
        boolean hasPrevious = pageCourse.hasPrevious();

        Map<String, Object> map = new HashMap<>();
        map.put("records", records);
        map.put("total", total);
        map.put("current", current);
        map.put("size", size);
        map.put("pages", pages);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    //根据课程id，编写sql语句查询课程信息
    @Override
    public CourseFrontVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }
}
