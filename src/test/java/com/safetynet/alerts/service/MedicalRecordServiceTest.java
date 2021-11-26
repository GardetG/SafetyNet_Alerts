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
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
@SpringBootTest
class MedicalRecordServiceTest {

  @Autowired
  private MedicalRecordService medicalRecordService;

  @MockBean
  MedicalRecordRepository medicalRecordRepository;

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
  void getAllMedicalRecordsTest() throws Exception {
    // GIVEN
    List<MedicalRecord> expectedList = List.of(medicalRecordTest, medicalRecordTest2);
    when(medicalRecordRepository.findAll()).thenReturn(expectedList);

    // WHEN
    List<MedicalRecord> actualList = medicalRecordService.getAll();

    // THEN
    assertThat(actualList).isEqualTo(expectedList);
    verify(medicalRecordRepository, times(1)).findAll();
  }

  @Test
  void getAllMedicalRecordsEmptyListTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findAll()).thenReturn(Collections.emptyList());

    // WHEN
    List<MedicalRecord> actualList = medicalRecordService.getAll();

    // THEN
    assertThat(actualList).isNotNull().isEmpty();
    verify(medicalRecordRepository, times(1)).findAll();
  }

  @Test
  void getMedicalRecordByNameTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString()))
            .thenReturn(medicalRecordTest);

    // WHEN
    MedicalRecord actualmedicalRecord = medicalRecordService.getByName("firstName", "lastName");

    // THEN
    assertThat(actualmedicalRecord).isEqualTo(medicalRecordTest);
    verify(medicalRecordRepository, times(1)).findByName("firstName", "lastName");
  }

  @Test
  void getMedicalRecordByNameWhenNotFoundTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.getByName("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Medical record of firstName lastName not found");
  }

  @Test
  void addMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(null)
            .thenReturn(medicalRecordTest);
    when(medicalRecordRepository.add(any(MedicalRecord.class))).thenReturn(true);

    // WHEN
    MedicalRecord actualmedicalRecord = medicalRecordService.add(medicalRecordTest);

    // THEN
    assertThat(actualmedicalRecord).isEqualTo(medicalRecordTest);
    verify(medicalRecordRepository, times(2)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(1)).add(medicalRecordTest);
  }

  @Test
  void addAlreadyExistingMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString()))
            .thenReturn(medicalRecordTest);

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.add(medicalRecordTest);
    })

            // THEN
            .isInstanceOf(ResourceAlreadyExistsException.class)
            .hasMessageContaining("Medical record of firstName lastName already exists");
    verify(medicalRecordRepository, times(1)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(0)).add(any(MedicalRecord.class));
  }

  @Test
  void addInvalidMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecord invalidMedicalRecord = new MedicalRecord("", "lastName1", 
            LocalDate.ofYearDay(1980, 1), Collections.emptyList(), Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.add(invalidMedicalRecord);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Firstname is mandatory");
  }

  @Test
  void updateMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecord updatedMedicalRecord = new MedicalRecord("firstName", "lastName", 
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2", "update"),  List.of("update"));
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(medicalRecordTest)
            .thenReturn(updatedMedicalRecord);
    when(medicalRecordRepository.update(any(MedicalRecord.class))).thenReturn(true);

    // WHEN
    MedicalRecord actualmedicalRecord = medicalRecordService.update(updatedMedicalRecord);

    // THEN
    assertThat(actualmedicalRecord).isEqualTo(updatedMedicalRecord);
    verify(medicalRecordRepository, times(2)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(1)).update(updatedMedicalRecord);
  }

  @Test
  void updateNotFoundMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecord updatedMedicalRecord = new MedicalRecord("firstName", "lastName", 
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2", "update"),  List.of("update"));
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.update(updatedMedicalRecord);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Medical record of firstName lastName not found");
    verify(medicalRecordRepository, times(1)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(0)).update(any(MedicalRecord.class));
  }

  @Test
  void updateInvalidMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecord invalidMedicalRecord = new MedicalRecord("", "lastName1", 
            LocalDate.ofYearDay(1980, 1), Collections.emptyList(), Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.update(invalidMedicalRecord);
    })

            // THEN
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining("Firstname is mandatory");
  }
 
  @Test
  void deleteMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString()))
            .thenReturn(medicalRecordTest);

    // WHEN
    medicalRecordService.delete("firstName", "lastName");

    // THEN
    verify(medicalRecordRepository, times(1)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(1)).delete(medicalRecordTest);
  }

  @Test
  void deleteNotFoundMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(null);

    // WHEN
    assertThatThrownBy(() -> {
      medicalRecordService.delete("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Medical record of firstName lastName not found");
    verify(medicalRecordRepository, times(1)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(0)).delete(any(MedicalRecord.class));
  }
  
}
