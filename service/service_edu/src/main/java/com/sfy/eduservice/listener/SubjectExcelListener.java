package com.sfy.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sfy.eduservice.entity.EduSubject;
import com.sfy.eduservice.entity.excel.SubjectData;
import com.sfy.eduservice.service.EduSubjectService;


public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    //因为SubjectExcelListener不能交给spring进行管理，需要自己new，不能注入其它对象
    private EduSubjectService subjectService;

    public SubjectExcelListener() {}

    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //读取excel内容，一行一行进行读取
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {

/*        if(subjectData == null) {
            throw new HuitongException(20001, "文件数据为空");
        }*/

        //一行一行读取，每次读取有两个值，第一个值是一级分类，第二个值是二级分类
        //添加一级分类
        EduSubject exitOneSubject = this.exitOneSubject(subjectService, subjectData.getOneSubjectName());
        if(exitOneSubject == null) { //没有相同的一级分类，进行添加
            exitOneSubject = new EduSubject();
            exitOneSubject.setParentId("0");
            exitOneSubject.setTitle(subjectData.getOneSubjectName());
            subjectService.save(exitOneSubject);
        }

        //添加二级分类
        String pid = exitOneSubject.getId();
        EduSubject exitTwoSubject = this.exitTwoSubject(subjectService, subjectData.getSecondSubjectName(), pid);
        if(exitTwoSubject == null) {
            exitTwoSubject = new EduSubject();
            exitTwoSubject.setParentId(pid);
            exitTwoSubject.setTitle(subjectData.getSecondSubjectName());
            subjectService.save(exitTwoSubject);
        }
    }

    //判断一级分类不能重复添加
    private EduSubject exitOneSubject(EduSubjectService subjectService, String name) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", "0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private EduSubject exitTwoSubject(EduSubjectService subjectService, String name, String pid) {
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title", name);
        wrapper.eq("parent_id", pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {}
}
