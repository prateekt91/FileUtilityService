package com.prat.fileutility.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "users")
public record Users(
        @MongoId
        String _id,
        String fname,
        String lname,
        String email,
        String userName,
        String phone,
        String location,
        String role,
        String department,
        String joindate,
        Boolean status,
        String avatar) {}
