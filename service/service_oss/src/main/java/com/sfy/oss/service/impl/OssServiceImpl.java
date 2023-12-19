package com.sfy.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sfy.oss.service.OssService;
import com.sfy.oss.utils.ConstantPropertiesUtils;
import org.apache.poi.hssf.record.DVALRecord;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    //上传头像到oss
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        //工具类取值
        String endpoint = ConstantPropertiesUtils.END_POINT;
        String accessKeyId = ConstantPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtils.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtils.BUCKET_NAME;

        try {
            //创建oss实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            //获取上传文件输入流
            InputStream inputStream = file.getInputStream();

            //获取文件的实际名称
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            String fileName = uuid + file.getOriginalFilename();

            //获取当前日期，作为路径
            String datePath = new DateTime().toString("yyyy/MM/dd");
            fileName = datePath + "/" + fileName;

            //调用oss方法实现上传，bucket名称，路径和文件名称，文件输入流
            ossClient.putObject(bucketName, fileName, inputStream);
            //关闭oss
            ossClient.shutdown();

            //把上传到oss之后文件路径返回，需要手动拼接出来
            //url规则：https://bucketName.endpoint/fileName;
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
