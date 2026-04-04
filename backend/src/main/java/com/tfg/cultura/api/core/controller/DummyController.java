package com.tfg.cultura.api.core.controller;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/dummy")
public class DummyController {

    private final MongoTemplate mongoTemplate;

    public DummyController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public String getDummyData() {
        return "This is some dummy data from the API!";
    }

    @GetMapping("/mongodb")
    public ResponseEntity<String> getMongodbData() {
        try {
            mongoTemplate.getDb().runCommand(new Document("ping", 1));
            return ResponseEntity.ok("MongoDB OK");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("MongoDB ERROR: " + e.getMessage());
        }
    }

}
