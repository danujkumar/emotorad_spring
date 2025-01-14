package com.emotorad.Emotorad.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Product")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class product {
    @Id
    private ObjectId Id;

    @DBRef
    private user primaryId;
    private List<String> items;
}
