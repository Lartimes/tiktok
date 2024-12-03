package com.lartimes.tiktok.service.impl;

import com.google.gson.Gson;
import com.lartimes.tiktok.config.QiNiuConfig;
import com.lartimes.tiktok.exception.BaseException;
import com.lartimes.tiktok.service.QiNiuFileService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/3 10:37
 */
@Service
public class QiNiuFileServiceImpl implements QiNiuFileService {
    private static final Logger LOG = LogManager.getLogger(QiNiuFileServiceImpl.class);

    @Autowired
    private QiNiuConfig qiNiuConfig;

    @Override
    public String getToken() {
        return qiNiuConfig.videoGetToken();
    }

    @Override
    public String uploadFile(File file) {
        LOG.info("上传的文件 : {}", file);
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            Response response = uploadManager.put(file, null, qiNiuConfig.videoGetToken());
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return putRet.key;
        } catch (QiniuException ex) {
            if (ex.response != null) {
                System.err.println(ex.response);
                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
            throw new BaseException(ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String url) {

//        BucketManager bucketManager = new BucketManager(auth, cfg);
//        try {
//            bucketManager.delete(bucket, key);
//        } catch (QiniuException ex) {
//            //如果遇到异常，说明删除失败
//            System.err.println(ex.code());
//            System.err.println(ex.response.toString());
//        }
    }

    @Override
    public FileInfo getFileInfo(String url) {
        Configuration cfg = new Configuration(Region.autoRegion());
        Auth auth = qiNiuConfig.getAuth();
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            FileInfo fileInfo = bucketManager.stat(qiNiuConfig.getBucket(),
                    url);
            LOG.info("获取到fileInfo : {}", fileInfo);
            return fileInfo;
        } catch (QiniuException ex) {
            LOG.info("获取fileInfo失败 : {}", ex.response.getInfo());
            System.err.println(ex.response);
        }
        return null;
    }


}
