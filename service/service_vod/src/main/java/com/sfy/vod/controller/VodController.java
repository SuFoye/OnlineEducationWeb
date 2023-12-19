package com.sfy.vod.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.sfy.commonutils.R;
import com.sfy.servicebase.exceptionhandler.HuitongException;
import com.sfy.vod.service.VodService;
import com.sfy.vod.utils.ConstantsVodUtils;
import com.sfy.vod.utils.InitVodClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/eduvod/video")

public class VodController {

    @Autowired
    private VodService vodService;

    //上传视频到阿里云
    @PostMapping("uploadVideo")
    public R uploadVideo(MultipartFile file) {
        //返回上传视频的id
        String videoId = vodService.uploadVideoAly(file);
        return R.ok().data("videoId", videoId);
    }

    //根据视频id删除阿里云中对应的视频
    @DeleteMapping("removeAlyVideo/{videoId}")
    public R removeAlyVideo(@PathVariable String videoId) {
        try {
            //初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantsVodUtils.ACCESS_KEY_ID, ConstantsVodUtils.ACCESS_KEY_SECRET);
            //创建删除视频request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //向request设置视频id
            request.setVideoIds(videoId);
            //调用初始化对象的方法实现删除
            client.getAcsResponse(request);

            return R.ok();

        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    //删除多个阿里云中的视频，参数是多个视频id
    @DeleteMapping("deleteBatch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList) {
        vodService.removeMoreAlyVideo(videoIdList);
        return R.ok();
    }

    //根据视频id获取视频凭证
    @GetMapping("getPlayAuth/{videoId}")
    public R getPlayAuth(@PathVariable String videoId) {
        try{
            //创建初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantsVodUtils.ACCESS_KEY_ID, ConstantsVodUtils.ACCESS_KEY_SECRET);
            //创建获取凭证的request对象和response对象
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            //向request设置视频id
            request.setVideoId(videoId);
            //调用方法得到凭证
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();

            return R.ok().data("playAuth", playAuth);

        } catch (Exception e) {
            e.printStackTrace();
            throw new HuitongException(20001, "获取凭证失败");
        }
    }
}
