package com.emotorad.Emotorad.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.bson.Document;
import org.springframework.stereotype.Service;

import com.emotorad.Emotorad.entity.hashing;
import com.emotorad.Emotorad.entity.jsonParser;
import com.emotorad.Emotorad.entity.product;
import com.emotorad.Emotorad.entity.user;
import com.emotorad.Emotorad.repository.hashRepo;
import com.emotorad.Emotorad.repository.productRepo;
import com.emotorad.Emotorad.repository.userRepo;

@Service
public class Identification {

    @Autowired
    userRepo userUpdate;

    @Autowired
    hashRepo hash;

    @Autowired
    productRepo item;

    Map<String, Object> mp = new HashMap<String, Object>();

    // This is used to update the hash in mongo so that we can retrieve all
    // information of primary and secondary efficiently
    public void updateHash(user primaryId, user newPrimaryId, String email, String phone) {
        hashing h = hash.findByprimary(primaryId);
        h.setPrimary(newPrimaryId);
        HashSet<String> temp = h.getEmail();
        temp.add(email);
        h.setEmail(temp);

        temp = h.getPhone();
        temp.add(phone);
        h.setPhone(temp);

        hash.save(h);
    }

    public void updateHash(user primaryId, String email, String phone) {
        hashing h = new hashing();
        h.setPrimary(primaryId);

        HashSet<String> temp = new HashSet<String>();
        temp.add(email);
        h.setEmail(temp);

        temp = new HashSet<String>();
        temp.add(phone);
        h.setPhone(temp);

        hash.save(h);
    }

    //save the documents here
    private user saveDetails(String email, String phone, String linkPrecedence) {
        user u = new user();
        u.setEmail(email);
        u.setPhone(phone);
        u.setLinkPrecedence(linkPrecedence);

        return userUpdate.save(u);
    }

    //save the product only
    private void updateProduct(String items, user id, user change) {
        product p = item.findByprimaryId(id);
        List<String> a = p.getItems();
        a.add(items);
        p.setItems(a);
        if(change != null) p.setPrimaryId(change);
        item.save(p);
    }

    private void updateProduct(String items, user id)
    {
        product p = new product();
        p.setPrimaryId(id);
        ArrayList<String> temp = new ArrayList<String>();
        temp.add(items);
        p.setItems(temp);
        item.save(p);
    }

    // This function is used to update/transfer secondary contacts
    private void changeofSecondary(user primary, user secondary) {
        List<user> temp = primary.getSecondaryContacts();
        temp.add(primary);
        
        secondary.setLinkPrecedence("primary");
        secondary.setSecondaryContacts(temp);
        
        primary.setLinkPrecedence("secondary");
        primary.setLinkedId(secondary);
        primary.setSecondaryContacts(new ArrayList<user>());

        userUpdate.save(primary);
        userUpdate.save(secondary);
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
    public ResponseEntity<Map<String, Object>> endPoint(jsonParser u){
        try {
            user pc = userUpdate.findPrimaryUser(u.getEmail(), u.getPhone());
            user sec = userUpdate.findSecondaryUser(u.getEmail(), u.getPhone());

            if(pc != null){
                if (pc.getEmail().contains(u.getEmail()) && pc.getPhone().contains(u.getPhone())) {
                    updateProduct(u.getProduct(), pc, null);
                    return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
                }
                else
                {
                    user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                    updateProduct(u.getProduct(), pc, nc);
                    updateHash(pc, nc, u.getEmail(), u.getPhone());
                    changeofSecondary(pc, sec);
                    return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
                }
            }
            else if(sec != null){
                hashing hashed = hash.findByEmailOrPhone(sec.getEmail(), sec.getPhone());
                Optional<user> oldP = userUpdate.findById(hashed.getPrimary().getId());
                if(oldP != null)
                {
                    if(sec.getEmail().contains(oldP.get().getEmail()) && sec.getPhone().contains(oldP.get().getPhone()))
                    {
                        updateProduct(u.getProduct(), oldP.get(), sec);
                        changeofSecondary(oldP.get(), sec);
                        updateHash(oldP.get(), sec, sec.getEmail(), sec.getPhone());
                    }
                    else
                    {
                        user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                        updateProduct(u.getProduct(), oldP.get(), nc);
                        changeofSecondary(oldP.get(), nc);
                        updateHash(oldP.get(), nc, u.getEmail(), u.getPhone());
                    }
                    return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.SEE_OTHER);
                }
                else
                return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
            }
            else
            {
                user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                updateProduct(u.getProduct(), nc);
                updateHash(nc, nc.getEmail(), nc.getPhone());
                return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            
            mp.put("Error", "Something went wrong");
            return new ResponseEntity<Map<String,Object>>(mp, HttpStatus.BAD_REQUEST);
        }
    }
}
