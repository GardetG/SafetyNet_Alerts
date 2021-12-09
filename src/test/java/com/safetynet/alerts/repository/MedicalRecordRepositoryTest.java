package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.safetynet.alerts.model.MedicalRecord;
import java.time.LocalDate;
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
class MedicalRecordRepositoryTest {

  @Autowired
  MedicalRecordRepositoryImpl medicalRecordRepository;

  private MedicalRecord medicalRecordTest;
  private MedicalRecord medicalRecordTest2;

  @BeforeEach
  void setUp() throws Exception {
    medicalRecordTest = new MedicalRecord("firstName", "lastName", LocalDate.ofYearDay(1980, 1),
            List.of("med1", "med2"), Collections.emptyList());
    medicalRecordTest2 = new MedicalRecord("firstName2", "lastName2", LocalDate.ofYearDay(2000, 1),
            Collections.emptyList(), List.of("allg1"));
  }

  @Test
  void findAllMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    List<MedicalRecord> actualList = medicalRecordRepository.findAll();

    // THEN
    assertThat(actualList)
            .containsOnly(medicalRecordTest, medicalRecordTest2)
            .hasSize(2);
  }

  @Test
  void findAllMedicalRecordWhenNoMedicalRecordFoundTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(Collections.emptyList());

    // WHEN
    List<MedicalRecord> actualList = medicalRecordRepository.findAll();

    // THEN
    assertThat(actualList).isEmpty();
  }

  @Test
  void findMedicalRecordByNameTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    Optional<MedicalRecord> actualMedicalRecord = medicalRecordRepository
            .findByName("firstName", "lastName");

    // THEN
    assertThat(actualMedicalRecord).contains(medicalRecordTest);
  }

  @Test
  void findMedicalRecordWhenFirstnameNotFoundTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    Optional<MedicalRecord> actualMedicalRecord = medicalRecordRepository
            .findByName("Name3", "lastName");

    // THEN
    assertThat(actualMedicalRecord).isEmpty();
  }

  @Test
  void findMedicalRecordWhenLastnameNotFoundTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    Optional<MedicalRecord> actualMedicalRecord = medicalRecordRepository
            .findByName("firstName", "Name3");

    // THEN
    assertThat(actualMedicalRecord).isEmpty();
  }


  @Test
  void addMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest)));

    // WHEN
    boolean isSuccess = medicalRecordRepository.add(medicalRecordTest2);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(medicalRecordRepository.findAll())
            .hasSize(2)
            .containsOnly(medicalRecordTest, medicalRecordTest2);
  }

  @Test
  void addMedicalRecordWhenAlreadyExistsTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest)));

    // WHEN
    boolean isSuccess = medicalRecordRepository.add(medicalRecordTest);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(medicalRecordRepository.findAll())
            .hasSize(1)
            .containsOnly(medicalRecordTest);
  }
  
  @Test
  void updateMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest, medicalRecordTest2)));
    MedicalRecord updatedMedicalRecord = new MedicalRecord("firstName", "lastName", 
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2", "update"),  List.of("update"));

    // WHEN
    boolean isSuccess = medicalRecordRepository.update(updatedMedicalRecord);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(medicalRecordRepository.findAll()).hasSize(2)
            .doesNotContain(medicalRecordTest)
            .containsOnly(updatedMedicalRecord, medicalRecordTest2);
  }

  @Test
  void updateNotFoundMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest2)));
    MedicalRecord updatedMedicalRecord = new MedicalRecord("firstName", "lastName", 
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2", "update"),  List.of("update"));

    // WHEN
    boolean isSuccess = medicalRecordRepository.update(updatedMedicalRecord);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(medicalRecordRepository.findAll()).hasSize(1)
            .doesNotContain(updatedMedicalRecord)
            .containsOnly(medicalRecordTest2);
  }
  
  @Test
  void deleteMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest, medicalRecordTest2)));

    // WHEN
    boolean isSuccess = medicalRecordRepository.delete(medicalRecordTest);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(medicalRecordRepository.findAll()).hasSize(1)
            .doesNotContain(medicalRecordTest)
            .containsOnly(medicalRecordTest2);
  }

  @Test
  void deleteNotFoundMedicalRecordTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(new ArrayList<MedicalRecord>(
            List.of(medicalRecordTest2)));

    // WHEN
    boolean isSuccess = medicalRecordRepository.delete(medicalRecordTest);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(medicalRecordRepository.findAll()).hasSize(1)
            .containsOnly(medicalRecordTest2);
  }
  
  
}
