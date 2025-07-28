package com.prat.fileutility.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "fileDesc")
public record FileDesc (
        @Id
        String _id,
        String name,
        String type,
        long size,
        LocalDateTime modified,
        String location){
}
