package com.sfy.eduorder.client;

import com.sfy.commonutils.ordervo.UcenterMemberOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("service-ucenter")
public interface UcenterClient {

    //根据用户id获取用户信息，新建一个公共的UcenterMemberOrder类封装信息
    @PostMapping("/educenter/member/getUserInfo/{userId}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable("userId") String userId);
}
