package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.safetynet.alerts.model.Person;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
@SpringBootTest
class PersonRepositoryTest {

  @Autowired
  PersonRepositoryImpl personRepository;

  private Person personTest;
  private Person personTest2;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new Person("firstName", "lastName", "address", "city", "00001",
            "000-000-0001", "email@email.fr");
    personTest2 = new Person("firstName2", "lastName2", "address2", "city2", "00002",
            "000-000-0002", "email2@email.fr");

  }

  @Test
  void findAllPersonTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

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
    personRepository.setupRepository(Collections.emptyList());

    // WHEN
    List<Person> actualList = personRepository.findAll();

    // THEN
    assertThat(actualList).isEmpty();
  }

  @Test
  void findPersonByCityTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    List<Person> actualList = personRepository.findByCity("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personTest));
  }

  @Test
  void findPersonByCityNotFoundTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    List<Person> actualList = personRepository.findByCity("city9");

    // THEN
    assertThat(actualList).isEmpty();
  }
  
  @Test
  void findPersonByAddressTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    List<Person> actualList = personRepository.findByAddress("address");

    // THEN
    assertThat(actualList).isEqualTo(List.of(personTest));
  }

  @Test
  void findPersonByAddressNotFoundTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    List<Person> actualList = personRepository.findByAddress("address9");

    // THEN
    assertThat(actualList).isEmpty();
  }
  
  @Test
  void findPersonByNameTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    Optional<Person> actualPerson = personRepository.findByName("firstName", "lastName");

    // THEN
    assertThat(actualPerson.get()).isEqualTo(personTest);
  }

  @Test
  void findPersonWhenFirstnameNotFoundTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    Optional<Person> actualPerson = personRepository.findByName("Name3", "lastName");

    // THEN
    assertThat(actualPerson.isEmpty()).isTrue();
  }

  @Test
  void findPersonWhenLastnameNotFoundTest() {
    // GIVEN
    personRepository.setupRepository(List.of(personTest, personTest2));

    // WHEN
    Optional<Person> actualPerson = personRepository.findByName("firstName", "Name3");

    // THEN
    assertThat(actualPerson.isEmpty()).isTrue();
  }

  @Test
  void addPersonTest() {
    // GIVEN
    personRepository.setupRepository(new ArrayList<Person>(List.of(personTest)));

    // WHEN
    boolean isSuccess = personRepository.add(personTest2);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(personRepository.findAll()).hasSize(2)
            .containsExactly(personTest, personTest2);
  }

  @Test
  void updatePersonTest() {
    // GIVEN
    personRepository.setupRepository(new ArrayList<Person>(List.of(personTest, personTest2)));
    Person personTestUpdated = new Person("firstName", "lastName", "update", "update", "00003",
            "000.000.0003", "update@email.fr");

    // WHEN
    boolean isSuccess = personRepository.update(personTestUpdated);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(personRepository.findAll()).hasSize(2)
            .doesNotContain(personTest)
            .containsExactly(personTestUpdated, personTest2);
  }

  @Test
  void updateNotFoundPersonTest() {
    // GIVEN
    personRepository.setupRepository(new ArrayList<Person>(List.of(personTest2)));
    Person personTestUpdated = new Person("firstName", "lastName", "update", "update", "00003",
            "000.000.0003", "update@email.fr");

    // WHEN
    boolean isSuccess = personRepository.update(personTestUpdated);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(personRepository.findAll()).hasSize(1)
            .doesNotContain(personTestUpdated)
            .containsExactly(personTest2);
  }

  @Test
  void deletePersonTest() {
    // GIVEN
    personRepository.setupRepository(new ArrayList<Person>(List.of(personTest, personTest2)));

    // WHEN
    boolean isSuccess = personRepository.delete(personTest);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(personRepository.findAll()).hasSize(1)
            .doesNotContain(personTest)
            .containsExactly(personTest2);
  }

  @Test
  void deleteNotFoundPersonTest() {
    // GIVEN
    personRepository.setupRepository(new ArrayList<Person>(List.of(personTest2)));

    // WHEN
    boolean isSuccess = personRepository.delete(personTest);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(personRepository.findAll()).hasSize(1)
            .containsExactly(personTest2);
  }
  
}
