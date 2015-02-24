package com.myhealth.app.repository;

import com.myhealth.app.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Patient entity.
 */
public interface PatientRepository extends MongoRepository<Patient,String>{

}
