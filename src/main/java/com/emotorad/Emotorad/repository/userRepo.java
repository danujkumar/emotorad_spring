package com.emotorad.Emotorad.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.emotorad.Emotorad.entity.user;

public interface userRepo extends MongoRepository<user, ObjectId> {
        
}
