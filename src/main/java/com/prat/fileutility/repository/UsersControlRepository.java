package com.prat.fileutility.repository;

import com.prat.fileutility.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersControlRepository extends MongoRepository<Users, String> {

    Users findByUserName(String userName);
}
