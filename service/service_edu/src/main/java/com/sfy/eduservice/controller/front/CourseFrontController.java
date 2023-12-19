package com.sfy.eduservice.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sfy.commonutils.JwtUtils;
import com.sfy.commonutils.R;
import com.sfy.commonutils.ordervo.CourseFrontVoOrder;
import com.sfy.eduservice.client.OrdersClient;
import com.sfy.eduservice.entity.EduCourse;
import com.sfy.eduservice.entity.dto.front.CourseFront;
import com.sfy.eduservice.entity.vo.admin.chapter.Chapter;
import com.sfy.eduservice.entity.vo.front.CourseFrontVo;
import com.sfy.eduservice.service.EduChapterService;
import com.sfy.eduservice.service.EduCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eduservice/coursefront")

public class CourseFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrdersClient ordersClient;

    //条件查询带分页查询
    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page,
                                @PathVariable long limit,
                                @RequestBody(required = false) CourseFront courseFront) {
        Page<EduCourse> pageCourse = new Page<>(page, limit);
        Map<String, Object> map = courseService.getCourseFrontList(pageCourse, courseFront);
        return R.ok().data("course", map);
    }

    // 课程详情的方法
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request) {
        //根据课程id，编写sql语句查询课程信息
        CourseFrontVo courseFrontVo = courseService.getBaseCourseInfo(courseId);

        //根据课程id，查询章节和小节
        List<Chapter> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);

        //根据课程id和用户id查询当前课程是否已经支付过了
        boolean buyCourse = ordersClient.isBuyCourse(courseId, JwtUtils.getMemberIdByJwtToken(request));
        if(buyCourse == true) { //支付成功，修改课程购买数量+1
            EduCourse course = courseService.getById(courseId);
            course.setBuyCount(course.getBuyCount() + 1);
            courseService.updateById(course);
        }

        return R.ok().data("courseFrontVo", courseFrontVo).data("chapterVideoList", chapterVideoList).data("isBuy", buyCourse);
    }

    //根据课程id查询课程信息，新建一个公共类CourseFrontVoOrder封装信息
    @PostMapping("getCourseInforOrder/{courseId}")
    public CourseFrontVoOrder getCourseInfoOrder(@PathVariable String courseId) {
        CourseFrontVo courseInfo = courseService.getBaseCourseInfo(courseId);
        CourseFrontVoOrder courseFrontVoOrder = new CourseFrontVoOrder();
        BeanUtils.copyProperties(courseInfo, courseFrontVoOrder);
        return courseFrontVoOrder;
    }

}
