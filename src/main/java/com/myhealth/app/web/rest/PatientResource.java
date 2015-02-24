package com.myhealth.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.myhealth.app.domain.Patient;
import com.myhealth.app.repository.PatientRepository;
import com.myhealth.app.service.mongoGridFS.GridFSServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Patient.
 */
@RestController
@RequestMapping("/api")
public class PatientResource {

    private final Logger log = LoggerFactory.getLogger(PatientResource.class);

    @Inject
    private PatientRepository patientRepository;

    @Inject
    private GridFSServiceImpl gridFSRepository;

    /*
    @Inject
    private JcrSessionFactory jcrSessionFactory;
    */

    /**
     * POST  /patients -> Create a new patient.
     */
    @RequestMapping(value = "/patients",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Patient patient) {
        log.debug("REST request to save Patient : {}", patient);
        patientRepository.save(patient);
    }

    /**
     * GET  /patients -> get all the patients.
     */
    @RequestMapping(value = "/patients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Patient> getAll() {
        log.debug("REST request to get all Patients");
        return patientRepository.findAll();
    }

    /**
     * GET  /patients/:id -> get the "id" patient.
     */
    @RequestMapping(value = "/patients/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Patient> get(@PathVariable String id) {
        log.debug("REST request to get Patient : {}", id);
        return Optional.ofNullable(patientRepository.findOne(id))
            .map(patient -> new ResponseEntity<>(
                patient,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /patients/:id -> delete the "id" patient.
     */
    @RequestMapping(value = "/patients/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete Patient : {}", id);
        patientRepository.delete(id);
    }
}
