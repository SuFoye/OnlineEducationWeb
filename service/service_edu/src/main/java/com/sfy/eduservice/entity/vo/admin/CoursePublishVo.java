package com.sfy.eduservice.entity.vo.admin;

import lombok.Data;

@Data
public class CoursePublishVo {

    private String courseId;
    private String title;
    private String cover;
    private Integer lessonNum;
    private String description;
    private String subjectLevelOne;
    private String subjectLevelTwo;
    private String teacherName;
    private String price; //只用于显示
}
