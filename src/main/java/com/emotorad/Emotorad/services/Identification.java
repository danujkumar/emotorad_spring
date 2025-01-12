package com.emotorad.Emotorad.services;

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.emotorad.Emotorad.entity.user;
import com.emotorad.Emotorad.repository.hashRepo;
import com.emotorad.Emotorad.repository.userRepo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

@Service
public class Identification {

    @Autowired
    userRepo userUpdate;

    @Autowired
    hashRepo hash;

    // This is used to update the hash in mongo so that we can retrieve all
    // information of primary and secondary efficiently
    private void hashUpdater() {

    }

    //save the documents here
    private Document saveDetails(String email, String phone, String product, Object linkedId, String linkPrecedence) {
        Document newUser = new Document("email", email)
                .append("phone", phone)
                .append("product", product)
                .append("linkedId", new Document("$ref", "User").append("$id", linkedId))
                .append("linkPrecedence", linkPrecedence);

        userCollection.insertOne(newUser);

        return newUser;
    }



    // This function is used to update/transfer secondary contacts
    private void changeofSecondary() {

    }

    // This function is to update secondary contacts only
    private void secondaryUpdate(ObjectId primary, ObjectId secondary) {
        userCollection.updateOne(
            eq("_id", primary), 
            Updates.addEachToSet("secondaryContacts", java.util.Arrays.asList(secondary))
        );
    }

    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> hashCollection;

    private Document findPrimaryUser(String email, String phone) {
        return userCollection
                .find(or(eq("email", email), eq("phone", phone)))
                .filter(eq("linkPrecedence", "primary"))
                .first();
    }

    private Document findSecondaryUser(String email, String phone) {
        return userCollection
                .find(or(eq("email", email), eq("phone", phone)))
                .filter(eq("linkPrecedence", "secondary"))
                .first();
    }

    public void createHash(ObjectId primaryId, String email, String phone) {
        Document hashDocument = new Document("primary", primaryId)
                .append("email", Collections.singletonList(email)) 
                .append("phone", Collections.singletonList(phone));
        hashCollection.insertOne(hashDocument);        
    }


    public ResponseEntity<Map<String, Object>> buildResponse(Document primaryContact) {
        // Extract fields from the Document
        Object primaryContactId = primaryContact.getObjectId("_id");
        String email = primaryContact.getString("email");
        String phone = primaryContact.getString("phone");
        List<Object> secondaryContactIds = primaryContact.getList("secondaryContacts", Object.class);
        Object createdAt = primaryContact.get("createdAt");
        Object updatedAt = primaryContact.get("updatedAt");
        Object deletedAt = primaryContact.get("deletedAt");

        // Construct contactPairs
        List<Map<String, String>> contactPairs = new ArrayList<>();
        Map<String, String> contactMap = new HashMap<>();
        contactMap.put("email", email);
        contactMap.put("phone", phone);
        contactPairs.add(contactMap);

        // Build the response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("primaryContactId", primaryContactId);
        responseBody.put("contactPairs", contactPairs);
        responseBody.put("secondaryContactIds", secondaryContactIds);
        responseBody.put("createdAt", createdAt);
        responseBody.put("updatedAt", updatedAt);
        responseBody.put("deletedAt", deletedAt);

        // Return as ResponseEntity
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    

    // This is endpoint
    public ResponseEntity<Map<String, Object>> endPoint(user u){
        try {
            Document pc = findPrimaryUser( u.getEmail().toString(), u.getPhone().toString() );
            Document sec = findSecondaryUser( u.getEmail().toString(), u.getPhone().toString() );

            if(pc != null){
                if (pc.getString("linkedPrecedence").contains("primary") && 
                pc.getString("email").contains(u.getEmail().toString()) && 
                pc.getString("phone").contains(u.getPhone().toString())) {
                    Document sc = saveDetails(u.getEmail().toString(), u.getPhone().toString(), u.getProduct().toString(), pc.getObjectId("_id"), "secondary");
                    secondaryUpdate(pc.getObjectId("$id"), sc.getObjectId("$id"));
                    return buildResponse(pc);
                }
                else
                {

                }

            }
            else if(sec != null){

            }
            else
            {
                Document nc = saveDetails(u.getEmail().toString(), u.getPhone().toString(), u.getProduct().toString(), null, "primary");
                createHash(nc.getObjectId("$id"), u.getEmail().toString(), u.getPhone().toString());
                return buildResponse(nc);
            }
        } catch (Exception e) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("Error", "Something went wrong");
            return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
        }
    }
}
