package com.sfy.security.security;

import com.sfy.commonutils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    //有参构造
    public DefaultPasswordEncoder(int strength) {
    }

    //无参构造
    public DefaultPasswordEncoder() {
        this(-1);
    }

    //进行MD5加密
    @Override
    public String encode(CharSequence charSequence) {
        return MD5.encrypt(charSequence.toString());
    }

    //进行密码比对，encodedPassword是数据库存的加密后的数据，charSequence是前端传来的未加密数据
    @Override
    public boolean matches(CharSequence charSequence, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(charSequence.toString()));
    }
}
