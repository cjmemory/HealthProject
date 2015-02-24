package com.myhealth.app.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import com.myhealth.app.Application;
import com.myhealth.app.domain.Patient;
import com.myhealth.app.repository.PatientRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PatientResource REST controller.
 *
 * @see PatientResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PatientResourceTest {

    private static final String DEFAULT_FIRST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_FIRST_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_LAST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_NAME = "UPDATED_TEXT";

    @Inject
    private PatientRepository patientRepository;

    private MockMvc restPatientMockMvc;

    private Patient patient;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PatientResource patientResource = new PatientResource();
        ReflectionTestUtils.setField(patientResource, "patientRepository", patientRepository);
        this.restPatientMockMvc = MockMvcBuilders.standaloneSetup(patientResource).build();
    }

    @Before
    public void initTest() {
        patientRepository.deleteAll();
        patient = new Patient();
        patient.setFirstName(DEFAULT_FIRST_NAME);
        patient.setLastName(DEFAULT_LAST_NAME);
    }

    @Test
    public void createPatient() throws Exception {
        // Validate the database is empty
        assertThat(patientRepository.findAll()).hasSize(0);

        // Create the Patient
        restPatientMockMvc.perform(post("/api/patients")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patient)))
                .andExpect(status().isOk());

        // Validate the Patient in the database
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).hasSize(1);
        Patient testPatient = patients.iterator().next();
        assertThat(testPatient.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPatient.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
    }

    @Test
    public void getAllPatients() throws Exception {
        // Initialize the database
        patientRepository.save(patient);

        // Get all the patients
        restPatientMockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(patient.getId()))
                .andExpect(jsonPath("$.[0].firstName").value(DEFAULT_FIRST_NAME.toString()))
                .andExpect(jsonPath("$.[0].lastName").value(DEFAULT_LAST_NAME.toString()));
    }

    @Test
    public void getPatient() throws Exception {
        // Initialize the database
        patientRepository.save(patient);

        // Get the patient
        restPatientMockMvc.perform(get("/api/patients/{id}", patient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(patient.getId()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()));
    }

    @Test
    public void getNonExistingPatient() throws Exception {
        // Get the patient
        restPatientMockMvc.perform(get("/api/patients/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePatient() throws Exception {
        // Initialize the database
        patientRepository.save(patient);

        // Update the patient
        patient.setFirstName(UPDATED_FIRST_NAME);
        patient.setLastName(UPDATED_LAST_NAME);
        restPatientMockMvc.perform(post("/api/patients")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patient)))
                .andExpect(status().isOk());

        // Validate the Patient in the database
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).hasSize(1);
        Patient testPatient = patients.iterator().next();
        assertThat(testPatient.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPatient.getLastName()).isEqualTo(UPDATED_LAST_NAME);
    }

    @Test
    public void deletePatient() throws Exception {
        // Initialize the database
        patientRepository.save(patient);

        // Get the patient
        restPatientMockMvc.perform(delete("/api/patients/{id}", patient.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).hasSize(0);
    }
}
