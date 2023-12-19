package com.sfy.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.sfy.vod.service.VodService;
import com.sfy.vod.utils.ConstantsVodUtils;
import com.sfy.vod.utils.InitVodClient;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {
    //上传视频到阿里云
    @Override
    public String uploadVideoAly(MultipartFile file) {

        try {
            //fileName：上传文件原始名称
            String fileName = file.getOriginalFilename();
            //title：上传之后显示的名称
            String title = fileName.substring(0, fileName.lastIndexOf("."));
            //inputStream：上传文件输入流
            InputStream inputStream = file.getInputStream();

            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantsVodUtils.ACCESS_KEY_ID,
                    ConstantsVodUtils.ACCESS_KEY_SECRET, title, fileName, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            String videoId = null;
            if(response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回videoId同时会返回错误码。
                videoId = response.getVideoId();
            }

            return videoId;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //删除多个阿里云中的视频，参数是多个视频id
    @Override
    public void removeMoreAlyVideo(List videoIdList) {
        try {
            //初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantsVodUtils.ACCESS_KEY_ID, ConstantsVodUtils.ACCESS_KEY_SECRET);
            //创建删除视频的request对象
            DeleteVideoRequest request = new DeleteVideoRequest();
            //向request设置视频id：id1, id2,...
            String videoIds = StringUtils.join(videoIdList.toArray(), ",");
            request.setVideoIds(videoIds);
            //调用初始化对象方法实现删除
            client.getAcsResponse(request);

        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
