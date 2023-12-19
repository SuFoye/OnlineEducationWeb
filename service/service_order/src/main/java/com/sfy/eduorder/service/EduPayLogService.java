package com.sfy.eduorder.service;

import com.sfy.eduorder.entity.EduPayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
public interface EduPayLogService extends IService<EduPayLog> {

    //生成微信支付二维码
    Map<String, Object> createQR(String orderNo);

    //根据订单号获取支付状态
    Map<String, String> queryOrderStatus(String orderNo);

    //添加记录到支付表，并更新订单表的对应订单支付状态
    void updateOrderStatus(Map<String, String> map);
}
