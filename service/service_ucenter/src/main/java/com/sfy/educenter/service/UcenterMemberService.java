package com.sfy.educenter.service;

import com.sfy.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sfy.educenter.entity.dto.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author testjava
 * @since 2023-12-07
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    //登录的方法
    String login(UcenterMember member);

    //注册的方法
    void register(RegisterVo registerVo);

    //根据openid查询
    UcenterMember getMemberOpenId(String openid);

    //查询某一天注册人数
    Integer countRegisterDay(String day);
}
