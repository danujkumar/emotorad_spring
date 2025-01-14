package com.emotorad.Emotorad.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.emotorad.Emotorad.entity.hashing;
import com.emotorad.Emotorad.entity.user;

public interface hashRepo extends MongoRepository<hashing, ObjectId> {
    hashing findByprimary(user u);

    @Query("{ 'email': { $in: [?0] }, 'phone': { $in: [?1] } }")
    hashing findByEmailOrPhone(String email, String phone);
} 