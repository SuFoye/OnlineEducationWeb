package com.sfy.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sfy.commonutils.JwtUtils;
import com.sfy.commonutils.R;
import com.sfy.eduorder.entity.EduOrder;
import com.sfy.eduorder.service.EduOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
@RestController
@RequestMapping("/eduorder/order")

public class EduOrderController {

    @Autowired
    private EduOrderService orderService;

    //生成订单
    @PostMapping("createOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request) {
        //创建订单，返回订单号
        String userId = JwtUtils.getMemberIdByJwtToken(request);
        String orderNo = orderService.createOrders(courseId, userId);
        return R.ok().data("orderNo", orderNo);
    }

    //根据订单id查询订单信息
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId) {
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderId);
        EduOrder eduOrder = orderService.getOne(wrapper);
        return R.ok().data("eduOrder", eduOrder);
    }

    //根据课程id和用户id查询订单表中订单状态
    @GetMapping("isBuyCourse/{courseId}/{memberId}")
    public boolean isBuyCourse(@PathVariable String courseId, @PathVariable String memberId) {
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("member_id", memberId);
        wrapper.eq("status", "1"); //支付状态，1表示已经支付
        int count = orderService.count(wrapper);

        if(count > 0) { //已经支付
            return true;
        } else {
            return false;
        }
    }

}

