package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class PersonServiceTest {

  @Autowired
  private PersonService personService;

  @MockBean
  PersonRepository personRepository;

  private Person personTest;
  private Person personTest2;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new Person("firstName", "lastName", "address", "city", "0001", "000.000.0001",
            "email@mail.fr");
    personTest2 = new Person("firstName2", "lastName2", "address2", "city2", "0002", "000.000.0002",
            "email2@mail.fr");
  }

  @Test
  void getAllPersonsTest() throws Exception {
    // GIVEN
    List<Person> expectedList = List.of(personTest, personTest2);
    when(personRepository.findAll()).thenReturn(expectedList);

    // WHEN
    List<Person> actualList = personService.getAll();

    // THEN
    assertThat(actualList).isEqualTo(expectedList);
    verify(personRepository, times(1)).findAll();
  }

  @Test
  void getAllPersonsEmptyListTest() throws Exception {
    // GIVEN
    when(personRepository.findAll()).thenReturn(Collections.emptyList());

    // WHEN
    List<Person> actualList = personService.getAll();

    // THEN
    assertThat(actualList).isNotNull().isEmpty();
    verify(personRepository, times(1)).findAll();
  }

  @Test
  void getPersonByNameTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(personTest);

    // WHEN
    Person actualperson = personService.getByName("firstName", "lastName");

    // THEN
    assertThat(actualperson).isEqualTo(personTest);
    verify(personRepository, times(1)).findByName("firstName", "lastName");
  }

  @Test
  void getPersonByNameWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> {
      personService.getByName("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
  }

}
