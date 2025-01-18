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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // save the documents here
    private user saveDetails(String email, String phone, String linkPrecedence) {
        user u = new user();
        u.setEmail(email);
        u.setPhone(phone);
        u.setLinkPrecedence(linkPrecedence);

        return userUpdate.save(u);
    }

    // save the product only
    private void updateProduct(String items, user id, user change) {
        product p = item.findByprimaryId(id);
        List<String> a = p.getItems();
        a.add(items);
        p.setItems(a);
        if (change != null)
            p.setPrimaryId(change);
        item.save(p);
    }

    private void updateProduct(String items, user id) {
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

    //This method is used to show the json output
    public ResponseEntity<Map<String, Object>> buildResponse(user u, HttpStatus code) {
        String primaryContactId = u.getId().toString();
        String email = u.getEmail();
        String phone = u.getPhone();
        Object createdAt = u.getCreatedAt();
        Object updatedAt = u.getUpdatedAt();
        Object deletedAt = u.getDeletedAt();
    
        List<Map<String, String>> contactPairs = new ArrayList<>();
        Map<String, String> contactMap = new HashMap<>();
        contactMap.put("email", email);
        contactMap.put("phone", phone);
        contactPairs.add(contactMap);
    
        List<Map<String, String>> secondaryContacts = new ArrayList<>();
        for (user secondaryContact : u.getSecondaryContacts()) {
            Map<String, String> contactInfo = new HashMap<>();
            contactInfo.put("id", secondaryContact.getId().toString());
            contactInfo.put("email", secondaryContact.getEmail());
            contactInfo.put("phone", secondaryContact.getPhone());
            contactInfo.put("linkPrecedence", secondaryContact.getLinkPrecedence());
            secondaryContacts.add(contactInfo);
        }
    
        // Build the response body
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("primaryContactId", primaryContactId);
        responseBody.put("contactPairs", contactPairs);
        responseBody.put("secondaryContactIds", secondaryContacts);
        responseBody.put("createdAt", createdAt);
        responseBody.put("updatedAt", updatedAt);
        responseBody.put("deletedAt", deletedAt);
    
        return ResponseEntity.status(code).body(responseBody);
    }
    

    // This is endpoint
    @Transactional
    public ResponseEntity<Map<String, Object>> endPoint(jsonParser u) {
        try {
            user pc = userUpdate.findPrimaryUser(u.getEmail(), u.getPhone());
            List<user> secAnd = userUpdate.findSecondaryUserAnd(u.getEmail(), u.getPhone());
            List<user> secOr = userUpdate.findSecondaryUserOr(u.getEmail(), u.getPhone());


            if (pc != null) {
                if (pc.getEmail().contains(u.getEmail()) && pc.getPhone().contains(u.getPhone())) {
                    updateProduct(u.getProduct(), pc, null);
                    return buildResponse(pc, HttpStatus.OK);
                } else {
                    user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                    updateProduct(u.getProduct(), pc, nc);
                    updateHash(pc, nc, u.getEmail(), u.getPhone());
                    changeofSecondary(pc, nc);
                    return buildResponse(nc, HttpStatus.CREATED);
                }
            } else if (!secAnd.isEmpty()) {
                hashing hashed = hash.findByEmailOrPhone(secAnd.get(0).getEmail(), secAnd.get(0).getPhone());
                Optional<user> oldP = userUpdate.findById(hashed.getPrimary().getId());
                updateProduct(u.getProduct(), oldP.get(), secAnd.get(0));
                changeofSecondary(oldP.get(), secAnd.get(0));
                updateHash(oldP.get(), secAnd.get(0), secAnd.get(0).getEmail(), secAnd.get(0).getPhone());
                return buildResponse(secAnd.get(0), HttpStatus.OK);
            } else if (!secOr.isEmpty()) {
                hashing hashed = hash.findByEmailOrPhone(secOr.get(0).getEmail(), secOr.get(0).getPhone());
                Optional<user> oldP = userUpdate.findById(hashed.getPrimary().getId());
                user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                updateProduct(u.getProduct(), oldP.get(), nc);
                changeofSecondary(oldP.get(), nc);
                updateHash(oldP.get(), nc, u.getEmail(), u.getPhone());
                return buildResponse(nc, HttpStatus.CREATED);
            } else {
                user nc = saveDetails(u.getEmail(), u.getPhone(), "primary");
                updateProduct(u.getProduct(), nc);
                updateHash(nc, nc.getEmail(), nc.getPhone());
                return buildResponse(nc, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            mp.put("Error", "Something went wrong");
            return new ResponseEntity<Map<String, Object>>(mp, HttpStatus.BAD_REQUEST);
        }
    }
}
