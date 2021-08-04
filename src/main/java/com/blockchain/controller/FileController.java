package com.blockchain.controller;

import com.blockchain.serviceImpl.FileUploadServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
  @Autowired
  private FileUploadServiceImpl fileUploadService;
    @PostMapping("/file/hash")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileUploadService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

