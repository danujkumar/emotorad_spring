package com.emotorad.Emotorad.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.emotorad.Emotorad.entity.jsonParser;
import com.emotorad.Emotorad.services.Identification;

@RestController
public class Identify {

    @Autowired
    private Identification identify;

    @GetMapping("/")
    public ResponseEntity<String> check(){
        return new ResponseEntity<String>("Welcome to Emotorad Spring Boot System...", HttpStatus.OK);
    }

    @PostMapping("/identify")
    public ResponseEntity<Map<String, Object>> endPoints(@RequestBody jsonParser u){
        return identify.endPoint(u);
    }
}
