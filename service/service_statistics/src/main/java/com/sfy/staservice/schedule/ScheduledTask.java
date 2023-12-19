package com.sfy.staservice.schedule;

import com.sfy.staservice.service.StatisticsDailyService;
import com.sfy.staservice.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService statisticsDailyService;

    //在每天凌晨1点，执行方法，对前一天的数据进行统计并加到数据库
    @Scheduled(cron = "0 0 1 * * ?")
    public void task() {
        //生成前一天的日期字符串 xxxx-xx-xx
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        statisticsDailyService.registerCount(day);
    }
}
