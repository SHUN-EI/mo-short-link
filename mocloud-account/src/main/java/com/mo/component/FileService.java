package com.mo.component;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by mo on 2022/2/14
 */
public interface FileService {

    String uploadUserImg(MultipartFile file);
}
