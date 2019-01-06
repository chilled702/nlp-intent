package com.open_nlp.poc.nlp.controller;

import com.open_nlp.poc.nlp.service.OpenNLPService;
import org.springframework.beans.factory.annotation.Autowired;
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
