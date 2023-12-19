package com.sfy.eduservice.service;

import com.sfy.eduservice.entity.EduChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sfy.eduservice.entity.vo.admin.chapter.Chapter;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
public interface EduChapterService extends IService<EduChapter> {

    //根据课程id查询课程大纲及对应的小节部分
    List<Chapter> getChapterVideoByCourseId(String courseId);

    //删除章节的方法，顺带删除小节
    boolean deleteChapter(String chapterId);

    //根据课程id删除章节
    void removeChapterByCourseId(String courseId);
}
