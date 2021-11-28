package com.safetynet.alerts.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
@SpringBootTest
class MedicalRecordTest {

  private MedicalRecord medicalRecordTest;

  @BeforeEach
  void setUp() throws Exception {
    medicalRecordTest = new MedicalRecord("firstName", "lastName", LocalDate.now(),
            Collections.emptyList(), Collections.emptyList());
  }

  @Test
  void getAgeTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.now().minusYears(20));

    // WHEN
    int actualAge = medicalRecordTest.getAge();

    // THEN
    assertThat(actualAge).isEqualTo(20);
  }

  @Test
  void getAgeFuturBirthDateTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.now().plusYears(20));

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordTest.getAge();
    })

            // THEN
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void isMinorTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.now().minusYears(10));

    // WHEN
    boolean isMinor = medicalRecordTest.isMinor();

    // THEN
    assertThat(isMinor).isTrue();
  }

  @Test
  void isMinorLimitTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.now().minusYears(18));

    // WHEN
    boolean isMinor = medicalRecordTest.isMinor();

    // THEN
    assertThat(isMinor).isTrue();
  }
  
  @Test
  void isNotMinorTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.now().minusYears(20));

    // WHEN
    boolean isMinor = medicalRecordTest.isMinor();

    // THEN
    assertThat(isMinor).isFalse();
  }
  
  @Test
  void isMinorInvalidTest() throws Exception {
    // GIVEN
    medicalRecordTest.setBirthdate(LocalDate.ofYearDay(9999, 1));

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordTest.isMinor();
    })

            // THEN
            .isInstanceOf(IllegalArgumentException.class);
  }
  
}
