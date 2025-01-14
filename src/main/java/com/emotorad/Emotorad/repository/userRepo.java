package com.emotorad.Emotorad.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.emotorad.Emotorad.entity.user;

public interface userRepo extends MongoRepository<user, ObjectId> {

    @Query("{ $or: [ { 'email': ?0 }, { 'phone': ?1 } ], 'linkPrecedence': 'primary' }")
    user findPrimaryUser(String email, String phone);

    @Query("{ $or: [ { 'email': ?0 }, { 'phone': ?1 } ], 'linkPrecedence': 'secondary' }")
    user findSecondaryUser(String email, String phone);
}
