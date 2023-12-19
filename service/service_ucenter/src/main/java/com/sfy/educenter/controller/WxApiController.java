package com.sfy.educenter.controller;

import com.google.gson.Gson;
import com.sfy.commonutils.JwtUtils;
import com.sfy.educenter.entity.UcenterMember;
import com.sfy.educenter.service.UcenterMemberService;
import com.sfy.educenter.utils.ConstantWxUtils;
import com.sfy.educenter.utils.HttpClientUtils;
import com.sfy.servicebase.exceptionhandler.HuitongException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.util.HashMap;

@Controller //这里没有配置 @RestController，只是请求地址，不用返回数据
@RequestMapping("/educenter/api/wx")

public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //生成微信扫描二维码
    @GetMapping("login")
    public String getWxCode() {
        //固定地址，后面拼接参数
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        //对redirect_url进行URLEncoder编码
        String redirectUri = ConstantWxUtils.WX_OPEN_REDIRECT_URI;
        try {
            redirectUri = URLEncoder.encode(redirectUri, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置%s的值
        String url = String.format(
                    baseUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    redirectUri,
                    ""
                );

        //重定向到请求微信地址
        return "redirect:" + url;
    }

    // 获取扫描人信息，添加数据，实现微信登录
    @GetMapping("callback")
    public String callback(String code, String state) {

        try {
            //1 获取code值（临时票据），去请求微信固定的地址，得到access_token和openid（扫描人微信唯一标识）
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                            "?appid=%s" +
                            "&secret=%s" +
                            "&code=%s" +
                            "&grant_type=authorization_code";

            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code);

            //2 使用httpclient发送请求（模拟浏览器），得到返回结果
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //将accessTokenInfo的json字符串，转成map，取出access_token和openid
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String access_token = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");

            //4 根据openid查询数据库，有就直接实现登录，没有就获取扫码人信息加到数据库（帮其注册）
            UcenterMember member = memberService.getMemberOpenId(openid);
            if(member == null) { // 表中没此人数据

                //3 拿着access_token和openid，访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";

                String UserInfoUrl = String.format(
                        baseUserInfoUrl,
                        access_token,
                        openid);

                String userInfo = HttpClientUtils.get(UserInfoUrl);
                //获取返回userInfo的json字符串中扫码人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname"); //昵称
                String headImgUrl = (String) userInfoMap.get("headimgurl"); //头像

                //添加到数据库
                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headImgUrl);
                memberService.save(member);
            }

            //cookie不能跨域，使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());

            //5 返回首页面，通过路径传递token字符串
            return "redirect:http://localhost:3000?token=" + jwtToken;

        } catch (Exception e) {
            e.printStackTrace();
            throw new HuitongException(20001, "登录失败");
        }
    }
}
