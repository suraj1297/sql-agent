package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LlmModel;


@Repository
public interface LlmModelRepository extends MongoRepository<LlmModel, String> {

}
