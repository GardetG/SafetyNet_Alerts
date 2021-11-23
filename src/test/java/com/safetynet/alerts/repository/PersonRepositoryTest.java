package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.model.Person;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PersonRepositoryTest {

  @Autowired
  PersonRepositoryImpl personRepository;

  private Person personTest;
  private Person personTest2;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new Person("firstName", "lastName", "address", "city", "00001",
            "000.000.0001", "email@email.fr");
    personTest2 = new Person("firstName2", "lastName2", "address2", "city2", "00002",
            "000.000.0002", "email2@email.fr");

  }

  @Test
  void findAllPersonTest() {
    // GIVEN
    personRepository.setPersonsList(List.of(personTest, personTest2));

    // WHEN
    List<Person> actualList = personRepository.findAll();

    // THEN
    assertThat(actualList)
            .containsOnly(personTest, personTest2)
            .hasSize(2);
  }

  @Test
  void findAllPersonWhenNoPersonFoundTest() {
    // GIVEN
    personRepository.setPersonsList(Collections.emptyList());

    // WHEN
    List<Person> actualList = personRepository.findAll();

    // THEN
    assertThat(actualList).isEmpty();
  }

  @Test
  void findPersonByNameTest() {
    // GIVEN
    personRepository.setPersonsList(List.of(personTest, personTest2));

    // WHEN
    Person actualPerson = personRepository.findByName("firstName", "lastName");

    // THEN
    assertThat(actualPerson).isEqualTo(personTest);
  }

  @Test
  void findPersonWhenFirstnameNotFoundTest() {
    // GIVEN
    personRepository.setPersonsList(List.of(personTest, personTest2));

    // WHEN
    Person actualPerson = personRepository.findByName("Name3", "lastName");

    // THEN
    assertThat(actualPerson).isNull();
  }

  @Test
  void findPersonWhenLastnameNotFoundTest() {
    // GIVEN
    personRepository.setPersonsList(List.of(personTest, personTest2));

    // WHEN
    Person actualPerson = personRepository.findByName("firstName", "Name3");

    // THEN
    assertThat(actualPerson).isNull();
  }

}
