package com.blockchain.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.blockchain.dto.DataCreateRequest;
import com.blockchain.model.Block;
import com.blockchain.repository.BlockRepository;
import com.blockchain.repository.DataInfoRepository;
import com.blockchain.service.BlockService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class BlockServiceImpl implements BlockService {

    public static final String PLEASE_PROVIDE_DATA_FILE_NAME = "Please provide data file name";
    public static final String SHA_256 = "SHA-256";
    public static final String DATA_NOT_AVAILABLE_FOR_GIVEN_FILE_NAME = "Data not available for given file name";
    @Autowired
    private AmazonS3 s3Client;
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private DataInfoRepository dataInfoRepository;

    @Value("${data.save.path}")
    private String filePath;

    @Override
    public String createBlock(DataCreateRequest dataCreateRequest) throws IOException, NotFoundException {
        byte[] content = new byte[0];
        S3ObjectInputStream inputStream = null;
        String blockHash = null;
        Block block = new Block();
        if (dataCreateRequest.getFileName() != null) {
            S3Object s3Object = s3Client.getObject(bucketName, dataCreateRequest.getFileName());
            if (s3Object != null) {
                inputStream = s3Object.getObjectContent();
                block.setDataName(s3Object.getKey());
            }

            if (inputStream != null) {
                content = IOUtils.toByteArray(inputStream);
            }

            //data

            String fileHash = toHexString(content);
            List<Block> blockList = blockRepository.findAllByOrderByTimestampDesc();
            Date date = new Date();
            if (blockList.isEmpty()) {
                blockHash = createHash("", fileHash, new Timestamp(date.getTime()));
                block.setData(fileHash);
                block.setPreviousHash("");
            } else {
                blockHash = createHash(blockList.get(0).getPreviousHash(), fileHash, new Timestamp(date.getTime()));
                block.setData(fileHash);
                block.setPreviousHash(blockList.get(0).getHash());
            }
            block.setTimestamp(new Timestamp(date.getTime()));
            block.setHash(blockHash);
            block.setFile(content);

            blockRepository.save(block);

        } else {
            throw new NotFoundException(PLEASE_PROVIDE_DATA_FILE_NAME);
        }
        return blockHash;
    }

    public String createHash(String previousHash, String data, Timestamp timestamp) {
        String text = previousHash + data
                + timestamp;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            MessageDigest shaDigest = MessageDigest.getInstance(SHA_256);
            byte[] bytes = shaDigest.digest(text.getBytes(StandardCharsets.UTF_8));

            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    public String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    @Override
    public String getData(String fileName) throws IOException, NotFoundException {
        String data = null;
        if (fileName != null) {
            List<byte[]> block = blockRepository.getDataByFileName(fileName);
            if (!block.isEmpty()) {
                data = new String(block.get(0), StandardCharsets.UTF_8);
                Path path = Paths.get(filePath + fileName);
                Files.write(path, block.get(0));

            } else {
                throw new NotFoundException(DATA_NOT_AVAILABLE_FOR_GIVEN_FILE_NAME);
            }
        } else {
            throw new NotFoundException(PLEASE_PROVIDE_DATA_FILE_NAME);
        }

        return data;
    }
}
