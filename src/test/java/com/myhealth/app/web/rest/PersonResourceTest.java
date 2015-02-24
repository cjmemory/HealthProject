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
import com.myhealth.app.domain.Person;
import com.myhealth.app.repository.PersonRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PersonResource REST controller.
 *
 * @see PersonResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PersonResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private PersonRepository personRepository;

    private MockMvc restPersonMockMvc;

    private Person person;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PersonResource personResource = new PersonResource();
        ReflectionTestUtils.setField(personResource, "personRepository", personRepository);
        this.restPersonMockMvc = MockMvcBuilders.standaloneSetup(personResource).build();
    }

    @Before
    public void initTest() {
        personRepository.deleteAll();
        person = new Person();
        person.setName(DEFAULT_NAME);
    }

    @Test
    public void createPerson() throws Exception {
        // Validate the database is empty
        assertThat(personRepository.findAll()).hasSize(0);

        // Create the Person
        restPersonMockMvc.perform(post("/api/persons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(person)))
                .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(1);
        Person testPerson = persons.iterator().next();
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    public void getAllPersons() throws Exception {
        // Initialize the database
        personRepository.save(person);

        // Get all the persons
        restPersonMockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(person.getId()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()));
    }

    @Test
    public void getPerson() throws Exception {
        // Initialize the database
        personRepository.save(person);

        // Get the person
        restPersonMockMvc.perform(get("/api/persons/{id}", person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(person.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    public void getNonExistingPerson() throws Exception {
        // Get the person
        restPersonMockMvc.perform(get("/api/persons/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePerson() throws Exception {
        // Initialize the database
        personRepository.save(person);

        // Update the person
        person.setName(UPDATED_NAME);
        restPersonMockMvc.perform(post("/api/persons")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(person)))
                .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(1);
        Person testPerson = persons.iterator().next();
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    public void deletePerson() throws Exception {
        // Initialize the database
        personRepository.save(person);

        // Get the person
        restPersonMockMvc.perform(delete("/api/persons/{id}", person.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Person> persons = personRepository.findAll();
        assertThat(persons).hasSize(0);
    }
}
