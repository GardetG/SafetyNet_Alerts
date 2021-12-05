package com.safetynet.alerts.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.safetynet.alerts.model.FireStation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
@SpringBootTest
class FireStationRepositoryTest {

  @Autowired
  FireStationRepositoryImpl fireStationRepository;

  private FireStation fireStationTest;
  private FireStation fireStationTest2;
  private FireStation fireStationTest3;

  @BeforeEach
  void setUp() throws Exception {
    fireStationTest = new FireStation(1, "address");
    fireStationTest2 = new FireStation(1, "address2");
    fireStationTest3 = new FireStation(2, "address2");
  }

  @Test
  void findAllFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStation> actualList = fireStationRepository.findAll();

    // THEN
    assertThat(actualList)
            .containsOnly(fireStationTest, fireStationTest2, fireStationTest3)
            .hasSize(3);
  }

  @Test
  void findAllFireStationWhenNoFireStationFoundTest() {
    // GIVEN
    fireStationRepository.setupRepository(Collections.emptyList());

    // WHEN
    List<FireStation> actualList = fireStationRepository.findAll();

    // THEN
    assertThat(actualList).isEmpty();
  }

  @Test
  void findFireStationByStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStation> actualList = fireStationRepository.findByStation(1);

    // THEN
    assertThat(actualList)
            .containsOnly(fireStationTest, fireStationTest2)
            .hasSize(2);
  }

  @Test
  void findFireStationByStationWhenNotFoundTest() {
    // GIVEN
    fireStationRepository.setupRepository(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStation> actualList = fireStationRepository.findByStation(9);

    // THEN
    assertThat(actualList).isEmpty();
  }

  @Test
  void findFireStationByAddressTest() {
    // GIVEN
    fireStationRepository.setupRepository(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStation> actualFireStations = fireStationRepository.findByAddress("address2");

    // THEN
    assertThat(actualFireStations)            
            .containsOnly(fireStationTest2, fireStationTest3)
            .hasSize(2);
  }

  @Test
  void findFireStationByAddressWhenNotFoundTest() {
    // GIVEN
    fireStationRepository.setupRepository(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStation> actualFireStations = fireStationRepository.findByAddress("address9");

    // THEN
    assertThat(actualFireStations).isEmpty();
  }


  @Test
  void addFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(fireStationTest)));

    // WHEN
    boolean isSuccess = fireStationRepository.add(fireStationTest2);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(fireStationRepository.findAll())
            .hasSize(2)
            .containsOnly(fireStationTest, fireStationTest2);
  }

  @Test
  void addFireStationWhenAlreadyExistsTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(fireStationTest)));

    // WHEN
    boolean isSuccess = fireStationRepository.add(fireStationTest);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(fireStationRepository.findAll())
            .hasSize(1)
            .containsOnly(fireStationTest);
  }
  
  @Test
  void updateFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(
            fireStationTest, fireStationTest2)));
    FireStation fireStationTestUpdated = new FireStation(9, "address");

    // WHEN
    boolean isSuccess = fireStationRepository.update(fireStationTestUpdated);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(fireStationRepository.findAll()).hasSize(2)
            .doesNotContain(fireStationTest)
            .containsOnly(fireStationTestUpdated, fireStationTest2);
  }

  @Test
  void updateNotFoundFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(fireStationTest2)));
    FireStation fireStationTestUpdated = new FireStation(9, "address9");

    // WHEN
    boolean isSuccess = fireStationRepository.update(fireStationTestUpdated);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(fireStationRepository.findAll()).hasSize(1)
            .doesNotContain(fireStationTestUpdated)
            .containsOnly(fireStationTest2);
  }
  
  @Test
  void deleteFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(
            fireStationTest, fireStationTest2)));

    // WHEN
    boolean isSuccess = fireStationRepository.delete(fireStationTest);

    // THEN
    assertThat(isSuccess).isTrue();
    assertThat(fireStationRepository.findAll()).hasSize(1)
            .doesNotContain(fireStationTest)
            .containsOnly(fireStationTest2);
  }

  @Test
  void deleteNotFoundFireStationTest() {
    // GIVEN
    fireStationRepository.setupRepository(new ArrayList<FireStation>(List.of(
            fireStationTest2)));

    // WHEN
    boolean isSuccess = fireStationRepository.delete(fireStationTest);

    // THEN
    assertThat(isSuccess).isFalse();
    assertThat(fireStationRepository.findAll()).hasSize(1)
            .containsOnly(fireStationTest2);
  }
}
