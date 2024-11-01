package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.TrainData;

@Repository
public interface TrainDataRepository extends MongoRepository<TrainData, String> {
	
	List<TrainData> findBySchemaId(String schemaId);
	
}
