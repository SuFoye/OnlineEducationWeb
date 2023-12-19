package com.sfy.eduorder.controller;


import com.sfy.commonutils.R;
import com.sfy.eduorder.service.EduPayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
@RestController
@RequestMapping("/eduorder/paylog")

public class EduPayLogController {

    @Autowired
    private EduPayLogService payLogService;

    //根据订单号，生成微信支付二维码
    @GetMapping("createNative/{orderNo}")
    public R CreatePayQR(@PathVariable String orderNo) {
        //返回信息，包含二维码地址，还有其它信息
        Map<String, Object> map =payLogService.createQR(orderNo);
        return R.ok().data("Info", map);
    }

    //根据订单号获取支付状态
    @GetMapping("queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        Map<String, String> map = payLogService.queryOrderStatus(orderNo);
        if(map == null) {
            return R.error().message("支付出错了");
        }
        if(map.get("trade_state").equals("SUCCESS")) { //支付成功
            //添加记录到支付表，并更新订单表的对应订单支付状态
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }

        return R.ok().code(25000).message("支付中");
    }

}

