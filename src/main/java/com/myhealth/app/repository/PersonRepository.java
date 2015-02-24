package com.myhealth.app.repository;

import com.myhealth.app.domain.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Person entity.
 */
public interface PersonRepository extends MongoRepository<Person,String>{

}
