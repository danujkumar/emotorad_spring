package com.emotorad.Emotorad.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "User")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class user {
    @Id
    private ObjectId Id;

    @NonNull
    private Integer email;
    @NonNull
    private Integer phone;
    @NonNull
    private String linkPrecedence;

    @Builder.Default
    @DBRef
    private String linkedId = null;

    private String product;
    
    @Builder.Default
    private Date createdAt = new Date();  

    @Builder.Default
    private Date updatedAt = new Date(); 

    private Date deletedAt; 

    //Referencing to other collection will be carried out here
    @DBRef
    private List<user> secondaryContacts;
}
