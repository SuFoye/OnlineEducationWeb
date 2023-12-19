package com.sfy.eduorder.service.impl;

import com.sfy.commonutils.ordervo.CourseFrontVoOrder;
import com.sfy.commonutils.ordervo.UcenterMemberOrder;
import com.sfy.eduorder.client.EduClient;
import com.sfy.eduorder.client.UcenterClient;
import com.sfy.eduorder.entity.EduOrder;
import com.sfy.eduorder.mapper.EduOrderMapper;
import com.sfy.eduorder.service.EduOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sfy.eduorder.utils.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2023-12-10
 */
@Service
public class EduOrderServiceImpl extends ServiceImpl<EduOrderMapper, EduOrder> implements EduOrderService {

    @Autowired
    private EduClient eduClient;

    @Autowired
    private UcenterClient ucenterClient;

    //生成订单
    @Override
    public String createOrders(String courseId, String userId) {
        //通过远程调用根据用户id获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getUserInfoOrder(userId);
        //通过远程调用根据课程id获取课程信息
        CourseFrontVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);

        //添加到数据库
        EduOrder eduOrder = new EduOrder();
        eduOrder.setOrderNo(OrderNoUtil.getOrderNo()); //订单号
        eduOrder.setCourseId(courseId); //课程id
        eduOrder.setCourseTitle(courseInfoOrder.getTitle()); //课程标题
        eduOrder.setCourseCover(courseInfoOrder.getCover()); //课程封面
        eduOrder.setTeacherName(courseInfoOrder.getTeacherName()); //讲师名称
        eduOrder.setTotalFee(courseInfoOrder.getPrice()); //课程价格
        eduOrder.setMemberId(userId); //用户id
        eduOrder.setMobile(userInfoOrder.getMobile()); //用户手机号
        eduOrder.setNickname(userInfoOrder.getNickname()); //用户昵称
        eduOrder.setStatus(0); //支付状态，未支付0
        eduOrder.setPayType(1); //支付类型，微信1

        baseMapper.insert(eduOrder);

        //返回订单号
        return eduOrder.getOrderNo();
    }
}
