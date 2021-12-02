package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
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
class FireStationServiceTest {

  @Autowired
  private FireStationService fireStationService;

  @MockBean
  FireStationRepository fireStationRepository;

  private FireStation fireStationTest;
  private FireStation fireStationTest2;
  private FireStation fireStationTest3;
  private FireStationDto fireStationDto;
  private FireStationDto fireStationDto2;
  private FireStationDto fireStationDto3;

  @BeforeEach
  void setUp() throws Exception {
    fireStationTest = new FireStation(1, "address");
    fireStationDto = new FireStationDto(1, "address");
    fireStationTest2 = new FireStation(1, "address2");
    fireStationDto2 = new FireStationDto(1, "address2");
    fireStationTest3 = new FireStation(2, "address3");
    fireStationDto3 = new FireStationDto(2, "address3");
  }

  @Test
  void getAllFireStationsTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findAll())
            .thenReturn(List.of(fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    List<FireStationDto> actualList = fireStationService.getAll();

    // THEN
    assertThat(actualList).usingRecursiveComparison()
            .isEqualTo(List.of(fireStationDto, fireStationDto2, fireStationDto3));
    verify(fireStationRepository, times(1)).findAll();
  }

  @Test
  void getAllFireStationsEmptyListTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findAll()).thenReturn(Collections.emptyList());

    // WHEN
    List<FireStationDto> actualList = fireStationService.getAll();

    // THEN
    assertThat(actualList).isNotNull().isEmpty();
    verify(fireStationRepository, times(1)).findAll();
  }

  @Test
  void getFireStationByStationTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt()))
            .thenReturn(List.of(fireStationTest, fireStationTest2));

    // WHEN
    List<FireStationDto> actualList = fireStationService.getByStation(1);

    // THEN
    assertThat(actualList).usingRecursiveComparison()
            .isEqualTo(List.of(fireStationTest, fireStationTest2));
    verify(fireStationRepository, times(1)).findByStation(1);
  }

  @Test
  void getFireStationByStationWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.getByStation(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No addresses mapped for station 9 found");
  }

  @Test
  void getFireStationByAddressTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString()))
            .thenReturn(Optional.of(fireStationTest));

    // WHEN
    FireStationDto actualFireStation = fireStationService.getByAddress("address");

    // THEN
    assertThat(actualFireStation).usingRecursiveComparison().isEqualTo(fireStationDto);
    verify(fireStationRepository, times(1)).findByAddress("address");
  }

  @Test
  void getFireStationByAddressWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.getByAddress("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("address9 mapping not found");
  }

  @Test
  void addFireStationTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.empty())
            .thenReturn(Optional.of(fireStationTest));
    when(fireStationRepository.add(any(FireStation.class))).thenReturn(true);

    // WHEN
    FireStationDto actualfireStation = fireStationService.add(fireStationDto);

    // THEN
    assertThat(actualfireStation).usingRecursiveComparison().isEqualTo(fireStationDto);
    verify(fireStationRepository, times(2)).findByAddress("address");
    verify(fireStationRepository, times(1)).add(fireStationTest);
  }

  @Test
  void addAlreadyExistingFireStationTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.of(fireStationTest));

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.add(fireStationDto);
    })

            // THEN
            .isInstanceOf(ResourceAlreadyExistsException.class)
            .hasMessageContaining("address mapping for station 1 already exists");
    verify(fireStationRepository, times(1)).findByAddress("address");
    verify(fireStationRepository, times(0)).add(any(FireStation.class));
  }

  @Test
  void addInvalidFireStationTest() throws Exception {
    // GIVEN
    FireStationDto invalidFireStation = new FireStationDto(1, "");

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.add(invalidFireStation);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Address is mandatory");
  }

  @Test
  void updateFireStationTest() throws Exception {
    // GIVEN
    FireStation updatedFireStation = new FireStation(9, "address");
    FireStationDto updatedFireStationDto = new FireStationDto(9, "address");
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.of(fireStationTest))
            .thenReturn(Optional.of(updatedFireStation));
    when(fireStationRepository.update(any(FireStation.class))).thenReturn(true);

    // WHEN
    FireStationDto actualfireStation = fireStationService.update(updatedFireStationDto);

    // THEN
    assertThat(actualfireStation).usingRecursiveComparison().isEqualTo(updatedFireStationDto);
    verify(fireStationRepository, times(2)).findByAddress("address");
    verify(fireStationRepository, times(1)).update(updatedFireStation);
  }

  @Test
  void updateNotFoundFireStationTest() throws Exception {
    // GIVEN
    FireStationDto updatedFireStationDto = new FireStationDto(9, "address9");
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.update(updatedFireStationDto);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("address9 mapping not found");
    verify(fireStationRepository, times(1)).findByAddress("address9");
    verify(fireStationRepository, times(0)).update(any(FireStation.class));
  }

  @Test
  void updateInvalidFireStationTest() throws Exception {
    // GIVEN
    FireStationDto invalidFireStation = new FireStationDto(1, "");

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.update(invalidFireStation);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Address is mandatory");
  }
  
  
  @Test
  void deleteFireStationByStationTest() throws Exception {
    // GIVEN
    List<FireStation> fireStationList = List.of(fireStationTest, fireStationTest2);
    when(fireStationRepository.findByStation(anyInt())).thenReturn(fireStationList);

    // WHEN
    fireStationService.deleteByStation(1);

    // THEN
    verify(fireStationRepository, times(1)).findByStation(1);
    verify(fireStationRepository, times(1)).delete(fireStationTest);
    verify(fireStationRepository, times(1)).delete(fireStationTest2);
  }

  @Test
  void deleteByStationNotFoundFireStationTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.deleteByStation(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Station 9 mapping not found");
    verify(fireStationRepository, times(1)).findByStation(9);
    verify(fireStationRepository, times(0)).delete(any(FireStation.class));
  }
  
  @Test
  void deleteFireStationByAddressTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.of(fireStationTest));

    // WHEN
    fireStationService.deleteByAddress("address");

    // THEN
    verify(fireStationRepository, times(1)).findByAddress("address");
    verify(fireStationRepository, times(1)).delete(fireStationTest);
  }

  @Test
  void deleteByAddressNotFoundFireStationTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> {
      fireStationService.deleteByAddress("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("address9 mapping not found");
    verify(fireStationRepository, times(1)).findByAddress("address9");
    verify(fireStationRepository, times(0)).delete(any(FireStation.class));
  }
}
