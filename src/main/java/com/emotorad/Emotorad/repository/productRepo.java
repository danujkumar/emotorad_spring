package com.emotorad.Emotorad.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.emotorad.Emotorad.entity.product;
import com.emotorad.Emotorad.entity.user;

public interface productRepo extends MongoRepository<product, ObjectId> {
    product findByprimaryId(user u);
}
