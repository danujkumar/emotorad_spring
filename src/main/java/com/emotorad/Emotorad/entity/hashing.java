package com.emotorad.Emotorad.entity;

import java.util.HashSet;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Hashing")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class hashing {
    
    @Id
    private ObjectId id; 

    @NonNull
    @DBRef
    private user primary;  


    private HashSet<String> email;

    private HashSet<String> phone;
}
