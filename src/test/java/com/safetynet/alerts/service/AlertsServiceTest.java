package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FireStationCoverageDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("UnitTests")
@SpringBootTest
class AlertsServiceTest {

  @Autowired
  private AlertsService alertsService;

  @MockBean
  PersonRepository personRepository;
  
  @MockBean
  FireStationRepository fireStationRepository;
  
  @MockBean
  MedicalRecordRepository medicalRecordRepository;

  private Person childTest;
  private Person parent1Test;
  private Person parent2Test;

  private MedicalRecord childRecordTest;
  private MedicalRecord parent1RecordTest;
  private MedicalRecord parent2RecordTest;
  
  @BeforeEach
  void setUp() throws Exception {
    childTest = new Person("firstNameA", "lastName", "address", "city", "0001", "000-000-0001",
            "email@mail.fr");
    parent1Test = new Person("firstNameB", "lastName", "address", "city", "0002", "000-000-0001",
            "email2@mail.fr");
    parent2Test = new Person("firstNameC", "lastName", "address2", "city", "0003", "000-000-0002",
            "email2@mail.fr");
       
    childRecordTest = new MedicalRecord("firstNameA", "lastName", LocalDate.ofYearDay(2010, 1),
            Collections.emptyList(), Collections.emptyList());
  
    parent1RecordTest = new MedicalRecord("firstNameB", "lastName", LocalDate.ofYearDay(1980, 1),
            List.of("med1", "med2"), Collections.emptyList());

    parent2RecordTest = new MedicalRecord("firstNameC", "lastName", LocalDate.ofYearDay(1975, 1),
            Collections.emptyList(), List.of("allg1"));
    
  }

  @Test
  void getCommunityEmailTest() throws Exception {
    // GIVEN
    when(personRepository.findByCity(anyString())).thenReturn(List.of(
            childTest, parent1Test, parent2Test));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email@mail.fr", "email2@mail.fr"));
    verify(personRepository, times(1)).findByCity("city");
  }

  @Test
  void getCommunityEmailWithBlankEmailTest() throws Exception {
    // GIVEN
    Person personNullEmail = new Person("firstName", "lastName", "address", "city", "0001", 
            "000-000-0001", null);
    Person personBlankEmail = new Person("firstName", "lastName", "address", "city", "0001", 
            "000-000-0001", "  ");
    when(personRepository.findByCity(anyString())).thenReturn(List.of(
            parent1Test, personNullEmail, personBlankEmail));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email2@mail.fr"));
    verify(personRepository, times(1)).findByCity("city");
  }
  
  @Test
  void getCommunityEmailNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByCity(anyString())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getCommunityEmail("city9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No resident emails found for city9");
  }

  @Test
  void getPhoneAlertTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address9")));
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test)).thenReturn(Collections.emptyList());

    // WHEN
    List<String> actualList = alertsService.getPhoneAlert(1);

    // THEN
    assertThat(actualList).isEqualTo(List.of("000-000-0001"));
    verify(fireStationRepository, times(1)).findByStation(1);
    verify(personRepository, times(2)).findByAddress(anyString());
  }


  @Test
  void getPhoneAlertBlankPhoneTest() throws Exception {
    // GIVEN
    Person personNullEmail = new Person("firstName", "lastName", "address", "city", "0001", null,
            "email@mail.fr");
    Person personBlankEmail = new Person("firstName", "lastName", "address", "city", "0001", "  ",
            "email@mail.fr");
    when(fireStationRepository.findByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address9")));
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(parent1Test, personNullEmail, personBlankEmail))
          .thenReturn(Collections.emptyList());

    // WHEN
    List<String> actualList = alertsService.getPhoneAlert(1);

    // THEN
    assertThat(actualList).isEqualTo(List.of("000-000-0001"));
    verify(fireStationRepository, times(1)).findByStation(1);
    verify(personRepository, times(2)).findByAddress(anyString());
  }

  
  @Test
  void getPhoneAlertAddressesNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPhoneAlert(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No resident phone number found for station 9");
    verify(fireStationRepository, times(1)).findByStation(9);
    verify(personRepository, times(0)).findByAddress(anyString());
  }
  
  @Test
  void getPersonInfoTest() throws Exception {
    // GIVEN
    PersonInfoDto expectedDto = new PersonInfoDto("firstNameB", "lastName", "address", "41",
            List.of("med1", "med2"), Collections.emptyList(), null, "email2@mail.fr");
    when(personRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(parent1Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(parent1RecordTest));

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstNameB", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personRepository, times(1)).findByName("firstNameB", "lastName");
    verify(medicalRecordRepository, times(1)).findByName("firstNameB", "lastName");
  }

  @Test
  void getPersonInfoNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPersonInfo("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
    verify(personRepository, times(1)).findByName("firstName", "lastName");
    verify(medicalRecordRepository, times(0)).findByName(anyString(), anyString());
  }
  
  @Test
  void getPersonInfoMedicalRecordNotFoundTest() throws Exception {
    // GIVEN
    Person person = new Person("firstNameZ", "lastName", "address", "city", "", "", 
            "email@mail.fr");
    PersonInfoDto expectedDto = new PersonInfoDto("firstNameZ", "lastName", "address", 
            "Information not specified", List.of("Information not specified"), 
            List.of("Information not specified"), null, "email@mail.fr");
    when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(person));
    when(medicalRecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.empty());

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstNameZ", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personRepository, times(1)).findByName("firstNameZ", "lastName");
    verify(medicalRecordRepository, times(1)).findByName("firstNameZ", "lastName");
  }
  
  
  @Test
  void getChildAlertTest() throws Exception {
    // GIVEN  
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null),
              new PersonInfoDto("firstNameC", "lastName", null, null, null, null, null, null)));
    
    when(personRepository.findByAddress(anyString()))
            .thenReturn(List.of(childTest, parent1Test, parent2Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.of(parent2RecordTest));

    // WHEN
    ChildAlertDto actualDto = alertsService.childAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(3)).findByName(anyString(), anyString());
  }

  @Test
  void getChildAlertNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.childAlert("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found living at address9");
    verify(personRepository, times(1)).findByAddress("address9");
    verify(medicalRecordRepository, times(0)).findByName(anyString(), anyString());
  }
  
  @Test
  void getChildAlertMedicalRecordNotFoundTest() throws Exception {
    // GIVEN  
    Person parent2 = new Person("firstNameZ", "lastName", "address", "city", "", "", "");
    
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null),
              new PersonInfoDto("firstNameZ", "lastName", null, "Information not specified",
                      null, null, null, null)));
    
    when(personRepository.findByAddress(anyString()))
            .thenReturn(List.of(childTest, parent1Test, parent2));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.empty());

    // WHEN
    ChildAlertDto actualDto = alertsService.childAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(3)).findByName(anyString(), anyString());
  }
  
  @Test
  void getFireAlertTest() throws Exception {
    // GIVEN   
    FireAlertDto expectedDto = new FireAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11",
                      Collections.emptyList(), Collections.emptyList(), "000-000-0001", null),
              new PersonInfoDto("firstNameB", "lastName", null, "41",
                      List.of("med1", "med2"), Collections.emptyList(), "000-000-0001", null)),
              "[1]");
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest));
    when(fireStationRepository.findByAddress(anyString()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByAddress("address");
  }
  
  @Test
  void getFireAlertMappingNotFoundTest() throws Exception {
    // GIVEN   
    FireAlertDto expectedDto = new FireAlertDto(List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11",
                    Collections.emptyList(), Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41",
                    List.of("med1", "med2"), Collections.emptyList(), "000-000-0001", null)),
              "No station mapped for this address");
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest));
    when(fireStationRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByAddress("address");
  }

  @Test
  void getfireAlertNotFoundTest() throws Exception {
    // GIVEN
    when(personRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.fireAlert("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found living at address9");
    verify(personRepository, times(1)).findByAddress("address9");
    verify(medicalRecordRepository, times(0)).findByName(anyString(), anyString());
  }
  
  @Test
  void getFireAlertMedicalRecordNotFoundTest() throws Exception {
    // GIVEN  
    Person parent2 = new Person("firstNameZ", "lastName", "address", "city", "", "", "");
    
    FireAlertDto expectedDto = new FireAlertDto(List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11",
                    Collections.emptyList(), Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameZ", "lastName", null, "Information not specified",
                    List.of("Information not specified"), List.of("Information not specified"),
                    "", null)), "[1]");
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent2));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.empty());
    when(fireStationRepository.findByAddress(anyString()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByAddress("address");
  }
  
  @Test
  void getFloodAlertTest() throws Exception {
    // GIVEN   
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    FloodHouseholdDto floodHousehold2Dto = new FloodHouseholdDto("address2", List.of(
            new PersonInfoDto("firstNameC", "lastName", null, "46", Collections.emptyList(),
                    List.of("allg1"), "000-000-0002", null))); 
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.of(parent2RecordTest));
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto, floodHousehold2Dto));
    verify(personRepository, times(1)).findByAddress("address");
    verify(personRepository, times(1)).findByAddress("address2");
    verify(medicalRecordRepository, times(3)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(2)).findByStation(anyInt());
  }
  
  @Test
  void getFloodAlertMappingNotFoundTest() throws Exception {
    // GIVEN   
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.of(parent2RecordTest));
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(Collections.emptyList());

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 9));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto));
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(2)).findByStation(anyInt());
  }
  
  @Test
  void getFloodAlertMedicalRecordNotFoundTest() throws Exception {
    // GIVEN  
    Person parent2 = new Person("firstNameZ", "lastName", "address2", "city", "",
            "000-000-0002", "");
    
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    FloodHouseholdDto floodHousehold2Dto = new FloodHouseholdDto("address2", List.of(
            new PersonInfoDto("firstNameZ", "lastName", null, "Information not specified", 
                    List.of("Information not specified"), List.of("Information not specified"), 
                    "000-000-0002", null))); 
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.empty());
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto, floodHousehold2Dto));
    verify(personRepository, times(1)).findByAddress("address");
    verify(personRepository, times(1)).findByAddress("address2");
    verify(medicalRecordRepository, times(3)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(2)).findByStation(anyInt());
  }
  
  @Test
  void getFloodAlertNoResidentFoundForAnAddressTest() throws Exception {
    // GIVEN   
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(Collections.emptyList());
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest))
          .thenReturn(Optional.of(parent2RecordTest));
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto));
    verify(personRepository, times(1)).findByAddress("address");
    verify(personRepository, times(1)).findByAddress("address2");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(2)).findByStation(anyInt());
  }
  
  @Test
  void getFloodAlertNoResidentFoundTest() throws Exception {
    // GIVEN   
    when(personRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address9")))
          .thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.floodAlert(List.of(9, 10));
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents covered found for stations [9, 10]");
    verify(personRepository, times(1)).findByAddress("address9");
    verify(medicalRecordRepository, times(0)).findByName(anyString(), anyString());
  }
  
  @Test
  void fireStationCoverageTest() throws Exception {
    // GIVEN   
    FireStationCoverageDto expectedDto = new FireStationCoverageDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", "address", null,
                      null, null, "000-000-0001", null),
              new PersonInfoDto("firstNameB", "lastName", "address", null,
                      null, null, "000-000-0001", null)),
              1, 1, null);
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.of(parent1RecordTest));
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireStationCoverageDto actualDto = alertsService.fireStationCoverage(1);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByStation(1);
  }
  
  @Test
  void fireStationCoverageMedicalRecordNotFoundTest() throws Exception {
    // GIVEN   
    Person parent2 = new Person("firstNameZ", "lastName", "address", "city", "",
            "000-000-0002", "");
    
    FireStationCoverageDto expectedDto = new FireStationCoverageDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", "address", null,
                      null, null, "000-000-0001", null),
              new PersonInfoDto("firstNameZ", "lastName", "address", null,
                      null, null, "000-000-0002", null)),
              1, 0, 1);
    
    when(personRepository.findByAddress(anyString()))
          .thenReturn(List.of(childTest, parent2));
    when(medicalRecordRepository.findByName(anyString(), anyString()))
          .thenReturn(Optional.of(childRecordTest)).thenReturn(Optional.empty());
    when(fireStationRepository.findByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireStationCoverageDto actualDto = alertsService.fireStationCoverage(1);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personRepository, times(1)).findByAddress("address");
    verify(medicalRecordRepository, times(2)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByStation(1);
  }
  
  @Test
  void fireStationCoverageMappedAddressesNotFoundTest() throws Exception {
    // GIVEN   
    when(fireStationRepository.findByStation(anyInt())).thenReturn(Collections.emptyList());
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.fireStationCoverage(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents covered found for station 9");
    verify(personRepository, times(0)).findByAddress(anyString());
    verify(medicalRecordRepository, times(0)).findByName(anyString(), anyString());
    verify(fireStationRepository, times(1)).findByStation(9);
  }
}
