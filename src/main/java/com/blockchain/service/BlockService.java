package com.blockchain.service;

import com.blockchain.dto.DataCreateRequest;
import javassist.NotFoundException;

import java.io.IOException;

public interface BlockService {
    String createBlock(DataCreateRequest dataCreateRequest) throws IOException, NotFoundException;
    String getData(String fileName) throws IOException, NotFoundException;
}
