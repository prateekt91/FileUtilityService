package com.prat.fileutility.repository;

import com.prat.fileutility.model.FileDesc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileOperationRepository extends MongoRepository<FileDesc, String> {


    FileDesc findByName(String fileName);
}
