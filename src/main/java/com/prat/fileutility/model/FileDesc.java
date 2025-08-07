package com.prat.fileutility.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "fileDesc")
public record FileDesc (
        @MongoId
        String _id,
        String name,
        String type,
        long size,
        LocalDateTime modified,
        String location){
}
