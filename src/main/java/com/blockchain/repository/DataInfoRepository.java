package com.blockchain.repository;
import com.blockchain.model.DataInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataInfoRepository extends CrudRepository<DataInfo, Long> {
    @Query("select d.fileHash from DataInfo d where d.fileName = ?1 and d.available = true")
    String findFileHashByFileNameAndAvailable(String fileName);
}
