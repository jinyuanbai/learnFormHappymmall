package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @Classname FileServiceImpl
 * @Description
 * @Date 2019/3/20 9:17
 * @Created by godFather
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();

        if (StringUtils.isEmpty(fileName)) {
            logger.info("上传文件为空");
            return "";
        }
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf("."));
        String uploadFileName = UUID.randomUUID() + fileExtensionName;

        logger.info("开始上传文件，上传文件的文件名:{}，上传的路径:{},新文件名:{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            boolean flag = fileDir.setWritable(true);
            if (flag) {
                logger.info("设置写权限成功");
            } else {
                logger.info("设置写权限失败");
            }
            boolean dirFlag = fileDir.mkdirs();
            if (dirFlag) {
                logger.info("创建文件夹成功");
            } else {
                logger.info("创建文件夹失败");
            }
        }
        File targetFile = new File(path, uploadFileName);
        try {
            //文件已经上传成功
            file.transferTo(targetFile);

            //todo 将targetFile上传到我们的FTP服务器上
            //已经上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //todo 上传完之后，删除upload下面的文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }
}
