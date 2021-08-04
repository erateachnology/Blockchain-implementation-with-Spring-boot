package com.blockchain.serviceImpl;

import com.blockchain.model.DataInfo;
import com.blockchain.repository.DataInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class FileUploadServiceImpl {

    @Autowired
    private DataInfoRepository dataInfoRepository;

    public void uploadFile(MultipartFile file) {
        String shaChecksum = null;
        DataInfo dataInfo = new DataInfo();
        try {
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
            shaChecksum = getFileChecksum(shaDigest, file);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Insert into Data_Info
        dataInfo.setFileName(file.getOriginalFilename());
        dataInfo.setFileHash(shaChecksum);
        dataInfo.setAvailable(true);
        Date date = new Date();
        dataInfo.setTimestamp(new Timestamp(date.getTime()));
        dataInfoRepository.save(dataInfo);
    }

    public String getFileChecksum(MessageDigest digest, MultipartFile file) {
        FileInputStream fis = null;
        File files = null;
        String fileName = file.getOriginalFilename();
        String prefix = null;
        if (fileName != null) {
            prefix = fileName.substring(fileName.lastIndexOf("."));
        }

        try {

            if (fileName != null) {
                files = File.createTempFile(fileName, prefix);
            }
            if (files != null) {
                file.transferTo(files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            fis = new FileInputStream(files);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while (true) {
            try {
                if (!((bytesCount = fis.read(byteArray)) != -1)) break;
            } catch (IOException exx) {
                exx.printStackTrace();
            }
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        try {
            fis.close();
        } catch (IOException exxx) {
            exxx.printStackTrace();
        }

        //Get the hash's bytes
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();

    }
}
