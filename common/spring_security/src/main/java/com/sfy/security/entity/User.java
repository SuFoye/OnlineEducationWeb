package com.sfy.security.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private String nickName;

    private String salt; //用户头像

    private String token;
}
