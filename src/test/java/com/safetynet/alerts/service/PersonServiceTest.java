package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
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
    personTest = new Person("firstName", "lastName", "address", "city", "0001", "000-000-0001",
            "email@mail.fr");
    personTest2 = new Person("firstName2", "lastName2", "address2", "city2", "0002", "000-000-0002",
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
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest));

    // WHEN
    Person actualperson = personService.getByName("firstName", "lastName");

    // THEN
    assertThat(actualperson).isEqualTo(personTest);
    verify(personRepository, times(1)).findByName("firstName", "lastName");
  }

  @Test
  void getPersonByNameWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      personService.getByName("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
  }

  @Test
  void getPersonByCityTest() throws Exception {
    // GIVEN
    when(personRepository.findByCity(anyString())).thenReturn(List.of(personTest));

    // WHEN
    List<Person> actualList = personService.getByCity("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personTest));
    verify(personRepository, times(1)).findByCity("city");
  }

  @Test
  void getPersonByCityWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByCity(anyString())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      personService.getByCity("city9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found for city9");
  }
  
  @Test
  void getPersonByAddressTest() throws Exception {
    // GIVEN
    when(personRepository.findByAddress(anyString())).thenReturn(List.of(personTest));

    // WHEN
    List<Person> actualList = personService.getByAddress("address");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personTest));
    verify(personRepository, times(1)).findByAddress("address");
  }

  @Test
  void getPersonByAddressWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      personService.getByAddress("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found living at address9");
  }
  
  @Test
  void addPersonTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty())
            .thenReturn(Optional.of(personTest));
    when(personRepository.add(any(Person.class))).thenReturn(true);

    // WHEN
    Person actualperson = personService.add(personTest);

    // THEN
    assertThat(actualperson).isEqualTo(personTest);
    verify(personRepository, times(2)).findByName("firstName", "lastName");
    verify(personRepository, times(1)).add(personTest);
  }

  @Test
  void addAlreadyExistingPersonTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest));

    // WHEN
    assertThatThrownBy(() -> {
      personService.add(personTest);
    })

            // THEN
            .isInstanceOf(ResourceAlreadyExistsException.class)
            .hasMessageContaining("firstName lastName already exists");
    verify(personRepository, times(1)).findByName("firstName", "lastName");
    verify(personRepository, times(0)).add(any(Person.class));
  }

  @Test
  void addInvalidPersonTest() throws Exception {
    // GIVEN
    Person invalidPerson = new Person("", "lastName1", "address1", "city1", "0001",
            "000.000.0001", "email1@mail.fr");

    // WHEN
    assertThatThrownBy(() -> {
      personService.add(invalidPerson);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Firstname is mandatory");
  }

  @Test
  void updatePersonTest() throws Exception {
    // GIVEN
    Person updatedPerson = new Person("firstName", "lastName", "updated", "updated", "00001",
            "000.000.0001", "updated@mail.fr");
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest))
            .thenReturn(Optional.of(updatedPerson));
    when(personRepository.update(any(Person.class))).thenReturn(true);

    // WHEN
    Person actualperson = personService.update(updatedPerson);

    // THEN
    assertThat(actualperson).isEqualTo(updatedPerson);
    verify(personRepository, times(2)).findByName("firstName", "lastName");
    verify(personRepository, times(1)).update(updatedPerson);
  }

  @Test
  void updateNotFoundPersonTest() throws Exception {
    // GIVEN
    Person updatedPerson = new Person("firstName", "lastName", "updated", "updated", "00001",
            "000.000.0001", "updated@mail.fr");
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      personService.update(updatedPerson);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
    verify(personRepository, times(1)).findByName("firstName", "lastName");
    verify(personRepository, times(0)).update(any(Person.class));
  }

  @Test
  void updateInvalidPersonTest() throws Exception {
    // GIVEN
    Person invalidPerson = new Person("", "lastName1", "address1", "city1", "0001",
            "000.000.0001", "email1@mail.fr");

    // WHEN
    assertThatThrownBy(() -> {
      personService.update(invalidPerson);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Firstname is mandatory");
  }
 
  @Test
  void deletePersonTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest));

    // WHEN
    personService.delete("firstName", "lastName");

    // THEN
    verify(personRepository, times(1)).findByName("firstName", "lastName");
    verify(personRepository, times(1)).delete(personTest);
  }

  @Test
  void deleteNotFoundPersonTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      personService.delete("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
    verify(personRepository, times(1)).findByName("firstName", "lastName");
    verify(personRepository, times(0)).delete(any(Person.class));
  }
  
}
