package com.sfy.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfy.eduservice.entity.EduSubject;
import com.sfy.eduservice.entity.excel.SubjectData;
import com.sfy.eduservice.entity.vo.admin.subject.OneSubject;
import com.sfy.eduservice.entity.vo.admin.subject.TwoSubject;
import com.sfy.eduservice.listener.SubjectExcelListener;
import com.sfy.eduservice.mapper.EduSubjectMapper;
import com.sfy.eduservice.service.EduSubjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-11-22
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file, EduSubjectService subjectService) {
        try {
            //文件输入流
            InputStream inputStream = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(inputStream, SubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //查询所有一级分类 parent_id = 0
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id", "0");
        List<EduSubject> OneSubjectList = baseMapper.selectList(wrapperOne);

        //查询所有二级分类，parent_id != 0
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id", "0");
        List<EduSubject> TwoSubjectList = baseMapper.selectList(wrapperTwo);

        //创建list集合，用于存储最终封装的数据
        List<OneSubject> finalSubjectList = new ArrayList<>();

        //封装一级分类
        for (EduSubject eduSubject : OneSubjectList) {
            OneSubject oneSubject = new OneSubject();
            //oneSubject.setId(eduSubject.getId());
            //oneSubject.setTitle(eduSubject.getTitle());
            BeanUtils.copyProperties(eduSubject, oneSubject);
            //多个OneSubject放到finalSubjectList里面
            finalSubjectList.add(oneSubject);

            //封装二级分类
            List<TwoSubject> twoFinalSubjectList = new ArrayList<>();
            for (EduSubject subject : TwoSubjectList) {
                TwoSubject twoSubject = new TwoSubject();
                //判断二级分类的parent_id是否等于一级分类的id
                if(subject.getParentId().equals(eduSubject.getId())) {
                    BeanUtils.copyProperties(subject, twoSubject);
                    twoFinalSubjectList.add(twoSubject);
                }
            }

            //把一级下面所有二级分类放到一级分类中
            oneSubject.setChildren(twoFinalSubjectList);
        }

        return finalSubjectList;
    }
}
