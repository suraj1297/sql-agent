package com.example.demo.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Schema;

@Repository
public interface SchemaRepository extends MongoRepository<Schema, String> {
	
}

