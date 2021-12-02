package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.dto.PersonDto;
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
  private PersonDto personDto;
  private PersonDto personDto2;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new Person("firstName", "lastName", "address", "city", "0001", "000-000-0001",
            "email@mail.fr");
    personDto = new PersonDto("firstName", "lastName", "address", "city", "0001", "000-000-0001",
            "email@mail.fr");
    personTest2 = new Person("firstName2", "lastName2", "address2", "city2", "0002", "000-000-0002",
            "email2@mail.fr");
    personDto2 = new PersonDto("firstName2", "lastName2", "address2", "city2", "0002", 
            "000-000-0002", "email2@mail.fr");
  }

  @Test
  void getAllPersonsTest() throws Exception {
    // GIVEN
    when(personRepository.findAll()).thenReturn(List.of(personTest, personTest2));

    // WHEN
    List<PersonDto> actualList = personService.getAll();

    // THEN
    assertThat(actualList).isEqualTo(List.of(personDto, personDto2));
    verify(personRepository, times(1)).findAll();
  }

  @Test
  void getAllPersonsEmptyListTest() throws Exception {
    // GIVEN
    when(personRepository.findAll()).thenReturn(Collections.emptyList());

    // WHEN
    List<PersonDto> actualList = personService.getAll();

    // THEN
    assertThat(actualList).isNotNull().isEmpty();
    verify(personRepository, times(1)).findAll();
  }

  @Test
  void getPersonByNameTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest));

    // WHEN
    PersonDto actualperson = personService.getByName("firstName", "lastName");

    // THEN
    assertThat(actualperson).isEqualTo(personDto);
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
    List<PersonDto> actualList = personService.getByCity("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personDto));
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
    List<PersonDto> actualList = personService.getByAddress("address");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personDto));
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
    PersonDto actualperson = personService.add(personDto);

    // THEN
    assertThat(actualperson).isEqualTo(personDto);
    verify(personRepository, times(2)).findByName("firstName", "lastName");
    verify(personRepository, times(1)).add(personTest);
  }

  @Test
  void addAlreadyExistingPersonTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest));

    // WHEN
    assertThatThrownBy(() -> {
      personService.add(personDto);
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
    PersonDto invalidPerson = new PersonDto("", "lastName1", "address1", "city1", "0001",
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
    PersonDto updatedPersonDto = new PersonDto("firstName", "lastName", "updated", "updated", 
            "00001", "000.000.0001", "updated@mail.fr");
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(personTest))
            .thenReturn(Optional.of(updatedPerson));
    when(personRepository.update(any(Person.class))).thenReturn(true);

    // WHEN
    PersonDto actualperson = personService.update(updatedPersonDto);

    // THEN
    assertThat(actualperson).isEqualTo(updatedPersonDto);
    verify(personRepository, times(2)).findByName("firstName", "lastName");
    verify(personRepository, times(1)).update(updatedPerson);
  }

  @Test
  void updateNotFoundPersonTest() throws Exception {
    // GIVEN
    PersonDto updatedPersonDto = new PersonDto("firstName", "lastName", "updated", "updated",
            "00001", "000.000.0001", "updated@mail.fr");
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      personService.update(updatedPersonDto);
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
    PersonDto invalidPersonDto = new PersonDto("", "lastName1", "address1", "city1", "0001",
            "000.000.0001", "email1@mail.fr");

    // WHEN
    assertThatThrownBy(() -> {
      personService.update(invalidPersonDto);
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
