package com.blockchain.repository;

import com.blockchain.model.Block;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends CrudRepository<Block, Long> {

    List<Block> findAllByOrderByTimestampDesc();

    @Query("select b.file from Block b where b.dataName = ?1")
    List<byte[]> getDataByFileName(String fileName);
}
