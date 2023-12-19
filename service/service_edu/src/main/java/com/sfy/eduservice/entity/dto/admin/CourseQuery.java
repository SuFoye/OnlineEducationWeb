package com.sfy.eduservice.entity.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CourseQuery {

    @ApiModelProperty(value = "课程名称")
    private String title;

    @ApiModelProperty(value = "讲师id")
    private String teacherId;

    @ApiModelProperty(value = "一级分类id")
    private String subjectParentId;

    @ApiModelProperty(value = "二级分类id")
    private String subjectId;

    @ApiModelProperty(value = "课程状态 Draft未发布  Normal已发布")
    private String status;
}
