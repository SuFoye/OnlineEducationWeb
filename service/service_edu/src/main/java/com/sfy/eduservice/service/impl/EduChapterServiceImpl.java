package com.sfy.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sfy.eduservice.entity.EduChapter;
import com.sfy.eduservice.entity.EduVideo;
import com.sfy.eduservice.entity.vo.admin.chapter.Chapter;
import com.sfy.eduservice.entity.vo.admin.chapter.Video;
import com.sfy.eduservice.mapper.EduChapterMapper;
import com.sfy.eduservice.service.EduChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfy.eduservice.service.EduVideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-11-24
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService videoService; //注入小节service

    @Override
    public List<Chapter> getChapterVideoByCourseId(String courseId) {

        //根据课程id查询课程里面所有的章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id", courseId);
        List<EduChapter> eduChapters = baseMapper.selectList(wrapperChapter);

        //根据课程id查询对应的所有小节
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        List<EduVideo> eduVideos = videoService.list(wrapperVideo);

        //创建list，用于最终封装数据
        List<Chapter> finalList = new ArrayList<>();

        //遍历章节list进行封装
        for (EduChapter eduChapter : eduChapters) {
            Chapter chapter = new Chapter();
            BeanUtils.copyProperties(eduChapter, chapter);
            finalList.add(chapter);

            //创建集合封装章节的小节
            List<Video> videoList = new ArrayList<>();

            //遍历小节list进行封装
            for (EduVideo eduVideo : eduVideos) {
                //判断：小节的chapterId和章节的id一致
                if(eduVideo.getChapterId().equals(eduChapter.getId())) {
                    Video video = new Video();
                    BeanUtils.copyProperties(eduVideo, video);
                    videoList.add(video);
                }
            }

            //把封装好的小节集合放到对应章节对象中
            chapter.setChildren(videoList);
        }

        return finalList;
    }

    //删除章节的方法，顺带删除小节
    @Override
    public boolean deleteChapter(String chapterId) {
        int ret = 0;
        //根据章节id查询小节表，能查出数据，则不进行删除章节
        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId);
        int count = videoService.count(wrapper);
        if(count <= 0) { //没有小节数据，可以删除章节
            ret = baseMapper.deleteById(chapterId);
        }

        return ret > 0;
    }

    //根据课程id删除章节
    @Override
    public void removeChapterByCourseId(String courseId) {
        QueryWrapper<EduChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        baseMapper.delete(wrapper);
    }
}
