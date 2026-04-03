package com.tfg.cultura.api;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.MongoTemplate;

@RestController
@RequestMapping("/api/dummy")
public class DummyController {

    @Autowired
    private MongoTemplate mongoTemplate;


    @GetMapping
    public String getDummyData() {
        return "This is some dummy data from the API!";
    }

    @GetMapping("/mongodb")
    public String getMongodbData() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            return "MongoDB OK";
        } catch (Exception e) {
            return "MongoDB ERROR: " + e.getMessage();
        }
    }

}
