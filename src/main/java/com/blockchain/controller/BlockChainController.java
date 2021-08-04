package com.blockchain.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.blockchain.dto.DataCreateRequest;
import com.blockchain.dto.Response;
import com.blockchain.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class BlockChainController {

    static final Logger logger = LogManager.getLogger(BlockChainController.class.getName());
    public static final String BLOCK_CREATION_SUCCESSFUL_FOR = "Block creation successful for {}";
    public static final String BLOCK_CREATION_FAIL_ = "Block creation fail ";
    public static final String BLOCK_CREATION_START = "Block creation start";
    public static final String GET_DATA_FROM_BLOCK_START = "Get data from block start";
    public static final String GET_DATA_FROM_BLOCK_SUCCESSFUL_FOR = "Get data from block successful for {}";
    public static final String GET_DATA_FROM_BLOCK_FAIL_ = "Get data from block fail ";

    @Autowired
    private BlockService blockService;

    @PostMapping("/create/block")
    @Operation(summary = "Create data blocks")
    public ResponseEntity<Response> createDataBlock(@RequestBody DataCreateRequest dataCreateRequest) {
        logger.info(BLOCK_CREATION_START);
        Response response = new Response();
        try{
            response.setBlockHash(blockService.createBlock(dataCreateRequest));
            logger.info(BLOCK_CREATION_SUCCESSFUL_FOR, dataCreateRequest.getFileName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (AmazonS3Exception | IOException | NotFoundException | IllegalArgumentException e){
            response.setMessage(e.getMessage());
            logger.error(BLOCK_CREATION_FAIL_, e);
          return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/getData/{fileName}")
    @Operation(summary = "Get Data")
    public ResponseEntity<Response> getData(@PathVariable String fileName) {
        logger.info(GET_DATA_FROM_BLOCK_START);
        Response response = new Response();
        try{
           String data =  blockService.getData(fileName);
            logger.info(GET_DATA_FROM_BLOCK_SUCCESSFUL_FOR, fileName);
           response.setData(data);
           return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (IOException | AmazonS3Exception | NotFoundException e) {
            response.setMessage(e.getMessage());
            logger.error(GET_DATA_FROM_BLOCK_FAIL_, e);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
    }

}
