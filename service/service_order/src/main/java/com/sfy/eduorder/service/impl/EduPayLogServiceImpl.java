package com.sfy.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.sfy.eduorder.entity.EduOrder;
import com.sfy.eduorder.entity.EduPayLog;
import com.sfy.eduorder.mapper.EduPayLogMapper;
import com.sfy.eduorder.service.EduOrderService;
import com.sfy.eduorder.service.EduPayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfy.eduorder.utils.HttpClient;
import com.sfy.servicebase.exceptionhandler.HuitongException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
@Service
public class EduPayLogServiceImpl extends ServiceImpl<EduPayLogMapper, EduPayLog> implements EduPayLogService {

    @Autowired
    private EduOrderService orderService;

    //生成微信支付二维码
    @Override
    public Map<String, Object> createQR(String orderNo) {
        try{
            //根据订单号查询订单信息
            QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no", orderNo);
            EduOrder order = orderService.getOne(wrapper);

            //使用map设置生成二维码需要的参数
            Map<String, String> map = new HashMap<>();
            map.put("appid", "wx74862e0dfcf69954");
            map.put("mch_id", "1558950191"); //商户号
            map.put("nonce_str", WXPayUtil.generateNonceStr()); //生成随机字符串
            map.put("body", order.getCourseTitle()); //课程名称
            map.put("out_trade_no", orderNo); //订单号
            map.put("total_fee", order.getTotalFee().
                    multiply(new BigDecimal("100")).longValue() + "");
            map.put("spbill_create_ip", "127.0.0.1"); //支付的IP地址
            map.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify"); //回调地址
            map.put("trade_type", "NATIVE"); //生成二维码支付的类型

            //发送httpclient请求，传递xml格式参数，微信支付提供的固定的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式参数
            client.setXmlParam(WXPayUtil.generateSignedXml(
                    map, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb")); //商户key用于加密
            client.setHttps(true);
            //执行请求发送
            client.post();

            //得到发送请求返回的结果（xml格式），转成map集合（二维码地址）
            String content = client.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);

            //返回最终的数据（二维码地址和其它的订单信息）
            Map<String, Object> resultMap = new HashMap();
            resultMap.put("out_trade_no", orderNo);
            resultMap.put("courseId", order.getCourseId());
            resultMap.put("totalFee", order.getTotalFee());
            resultMap.put("resultCode", xmlToMap.get("result_code")); //发送二维码操作状态码
            resultMap.put("codeUrl", xmlToMap.get("code_url")); //二维码地址

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            throw new HuitongException(20001, "生成微信支付二维码失败");
        }
    }

    //根据订单号获取支付状态
    @Override
    public Map<String, String> queryOrderStatus(String orderNo) {
        try{
            //封装参数
            Map<String, String> map = new HashMap();
            map.put("appid", "wx74862e0dfcf69954");
            map.put("mch_id", "1558950191");
            map.put("out_trade_no", orderNo);
            map.put("nonce_str", WXPayUtil.generateNonceStr());

            //发送请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(map, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();

            //得到发送请求返回的内容（xml格式）
            String content = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //添加记录到支付表，并更新订单表的对应订单支付状态
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //从map获取订单号，查数据库
        String orderNo = map.get("out_trade_no");
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        EduOrder order = orderService.getOne(wrapper);

        //更新订单支付状态
        if(order.getStatus().intValue() == 1) {
            return;
        } else {
            order.setStatus(1); //1表示已经支付
            orderService.updateById(order);
        }

        //添加支付记录
        EduPayLog payLog = new EduPayLog();

        payLog.setOrderNo(orderNo); //订单号
        payLog.setPayTime(new Date()); //支付时间
        payLog.setPayType(1); //支付类型
        payLog.setTotalFee(order.getTotalFee()); //支付金额(分)
        payLog.setTradeState(map.get("trade_state")); //支付状态
        payLog.setTransactionId(map.get("transaction_id")); //流水号
        payLog.setAttr(JSONObject.toJSONString(map)); //其它属性

        baseMapper.insert(payLog);
    }
}
