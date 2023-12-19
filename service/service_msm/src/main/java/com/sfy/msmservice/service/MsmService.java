package com.sfy.msmservice.service;

import java.util.Map;

public interface MsmService {
    //发送短信的方法
    boolean sendCode(Map<String, Object> param, String phone);
}
