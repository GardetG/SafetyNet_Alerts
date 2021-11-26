package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.safetynet.alerts.model.MedicalRecord;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
    MedicalRecord actualMedicalRecord = medicalRecordRepository.findByName("firstName", "lastName");

    // THEN
    assertThat(actualMedicalRecord).isEqualTo(medicalRecordTest);
  }

  @Test
  void findMedicalRecordWhenFirstnameNotFoundTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    MedicalRecord actualMedicalRecord = medicalRecordRepository.findByName("Name3", "lastName");

    // THEN
    assertThat(actualMedicalRecord).isNull();
  }

  @Test
  void findMedicalRecordWhenLastnameNotFoundTest() {
    // GIVEN
    medicalRecordRepository.setupRepository(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    MedicalRecord actualMedicalRecord = medicalRecordRepository.findByName("firstName", "Name3");

    // THEN
    assertThat(actualMedicalRecord).isNull();
  }

}
