package com.priya.poc.nlp.controller;

import com.priya.poc.nlp.service.OpenNLPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class NLPController {

    @Autowired
    OpenNLPService nlpService;

    @PostMapping("/nlp-query")
    public String getNLPQuery(@RequestBody String userQuery){
        return nlpService.categorizeQuery(userQuery);
    }
}
