package com.sfy.staservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sfy.commonutils.R;
import com.sfy.staservice.client.UcenterClient;
import com.sfy.staservice.entity.StatisticsDaily;
import com.sfy.staservice.mapper.StatisticsDailyMapper;
import com.sfy.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-12-13
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    //统计某一天的注册人数，生成数据
    @Override
    public void registerCount(String day) {
        //添加记录之前删除表相同日期的数据
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.eq("date_calculated", day);
        if(baseMapper.selectOne(wrapper) != null) {
            baseMapper.delete(wrapper);
        }

        //远程调用得到某天注册的人数
        R registerR = ucenterClient.countRegister(day);
        Integer registerCount = (Integer) registerR.getData().get("registerCount");
        //把获取数据添加到数据的统计分析表
        StatisticsDaily statisticsDaily = new StatisticsDaily();
        statisticsDaily.setRegisterNum(registerCount); //注册人数
        statisticsDaily.setDateCalculated(day); //统计日期
        statisticsDaily.setVideoViewNum(RandomUtils.nextInt(100, 200)); //TODO，每日视频浏览人数
        statisticsDaily.setLoginNum(RandomUtils.nextInt(100, 200)); //TODO，每日登录人数
        statisticsDaily.setCourseNum(RandomUtils.nextInt(100, 200)); //TODO，每日新增课程数

        baseMapper.insert(statisticsDaily);
    }

    //图表显示，返回两部分数据，日期json数组，数量json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated", begin, end);
        wrapper.select("date_calculated", type); //动态列
        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        //封装数据，前端要求json数组，对应后端的list
        List<String> dateList = new ArrayList<>();
        List<Integer> numDateList = new ArrayList<>();

        for (StatisticsDaily statisticsDaily : staList) {
            //封装日期
            dateList.add(statisticsDaily.getDateCalculated());
            //封装对应的数量
            switch (type) {
                case "login_num":
                    numDateList.add(statisticsDaily.getLoginNum());
                    break;
                case "register_num":
                    numDateList.add(statisticsDaily.getRegisterNum());
                    break;
                case "video_view_num":
                    numDateList.add(statisticsDaily.getVideoViewNum());
                    break;
                case "course_num":
                    numDateList.add(statisticsDaily.getCourseNum());
                    break;
                default:
                    break;
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("numDateList", numDateList);

        return map;
    }
}
