package com.sfy.eduorder.service;

import com.sfy.eduorder.entity.EduOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
public interface EduOrderService extends IService<EduOrder> {

    //生成订单
    String createOrders(String courseId, String userId);
}
