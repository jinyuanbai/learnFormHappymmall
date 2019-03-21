package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Interfacename IFileService
 * @Description
 * @Date 2019/3/20 9:17
 * @Created by godFather
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
