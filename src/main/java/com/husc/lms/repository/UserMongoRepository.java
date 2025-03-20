package com.husc.lms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.husc.lms.entity.UserMongo;




@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {

}
