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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
  PersonService personService;
  
  @MockBean
  FireStationService fireStationService;
  
  @MockBean
  MedicalRecordService medicalRecordService;

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
    when(personService.getByCity(anyString())).thenReturn(List.of(
            childTest, parent1Test, parent2Test));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email@mail.fr", "email2@mail.fr"));
    verify(personService, times(1)).getByCity("city");
  }

  @Test
  void getCommunityEmailWithBlankEmailTest() throws Exception {
    // GIVEN
    Person personNullEmail = new Person("firstName", "lastName", "address", "city", "0001", 
            "000-000-0001", null);
    Person personBlankEmail = new Person("firstName", "lastName", "address", "city", "0001", 
            "000-000-0001", "  ");
    when(personService.getByCity(anyString())).thenReturn(List.of(
            parent1Test, personNullEmail, personBlankEmail));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email2@mail.fr"));
    verify(personService, times(1)).getByCity("city");
  }
  
  @Test
  void getCommunityEmailNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getByCity(anyString())).thenThrow(
            new ResourceNotFoundException("No residents found for city9"));

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
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address2")));
    when(personService.getByAddress(anyString())).thenReturn(List.of(
            childTest, parent1Test, parent2Test)).thenThrow(
                    new ResourceNotFoundException("No residents found living at address2"));

    // WHEN
    List<String> actualList = alertsService.getPhoneAlert(1);

    // THEN
    assertThat(actualList).isEqualTo(List.of("000-000-0001", "000-000-0002"));
    verify(fireStationService, times(1)).getByStation(1);
    verify(personService, times(2)).getByAddress(anyString());
  }


  @Test
  void getPhoneAlertBlankPhoneTest() throws Exception {
    // GIVEN
    Person personNullEmail = new Person("firstName", "lastName", "address", "city", "0001", null,
            "email@mail.fr");
    Person personBlankEmail = new Person("firstName", "lastName", "address", "city", "0001", "  ",
            "email@mail.fr");
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address2")));
    when(personService.getByAddress(anyString())).thenReturn(List.of(
            parent1Test, personNullEmail, personBlankEmail)).thenThrow(
                    new ResourceNotFoundException("No residents found living at address2"));

    // WHEN
    List<String> actualList = alertsService.getPhoneAlert(1);

    // THEN
    assertThat(actualList).isEqualTo(List.of("000-000-0001"));
    verify(fireStationService, times(1)).getByStation(1);
    verify(personService, times(2)).getByAddress(anyString());
  }

  
  @Test
  void getPhoneAlertAddressesNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenThrow(
            new ResourceNotFoundException("No addresses mapped for station 9 found"));

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPhoneAlert(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No resident phone number found for station 9");
    verify(fireStationService, times(1)).getByStation(9);
    verify(personService, times(0)).getByAddress(anyString());
  }
  
  @Test
  void getPersonInfoTest() throws Exception {
    // GIVEN
    PersonInfoDto expectedDto = new PersonInfoDto("firstNameB", "lastName", "address", "41",
            List.of("med1", "med2"), Collections.emptyList(), null, null);
    when(personService.getByName(anyString(), anyString())).thenReturn(parent1Test);
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(parent1RecordTest);

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstNameB", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personService, times(1)).getByName("firstNameB", "lastName");
    verify(medicalRecordService, times(1)).getByName("firstNameB", "lastName");
  }

  @Test
  void getPersonInfoNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getByName(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException("firstName lastName not found"));
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPersonInfo("firstName", "lastName");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("firstName lastName not found");
    verify(personService, times(1)).getByName("firstName", "lastName");
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
  }
  
  @Test
  void getPersonInfoMedicalRecordNotFoundTest() throws Exception {
    // GIVEN
    Person person = new Person("firstNameZ", "lastName", "address", "city", "", "", "");
    PersonInfoDto expectedDto = new PersonInfoDto("firstNameZ", "lastName", "address", 
            "Information not specified", List.of("Information not specified"), 
            List.of("Information not specified"), null, null);
    when(personService.getByName(anyString(), anyString())).thenReturn(person);
    when(medicalRecordService.getByName(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException("Medical record of firstName lastName not found"));

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstNameZ", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personService, times(1)).getByName("firstNameZ", "lastName");
    verify(medicalRecordService, times(1)).getByName("firstNameZ", "lastName");
  }
  
  
  @Test
  void getChildAlertTest() throws Exception {
    // GIVEN  
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameC", "lastName", null, null, null, null, null, null),
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null)));
    
    when(personService.getByAddress(anyString()))
            .thenReturn(List.of(childTest, parent1Test, parent2Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest).thenReturn(parent2RecordTest);

    // WHEN
    ChildAlertDto actualDto = alertsService.childAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(3)).getByName(anyString(), anyString());
  }

  @Test
  void getChildAlertNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getByAddress(anyString())).thenThrow(
            new ResourceNotFoundException("No residents found living at address9"));
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.childAlert("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found living at address9");
    verify(personService, times(1)).getByAddress("address9");
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
  }
  
  @Test
  void getChildAlertMedicalRecordNotFoundTest() throws Exception {
    // GIVEN  
    Person parent2 = new Person("firstNameZ", "lastName", "address", "city", "", "", "");
    
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameZ", "lastName", null, "Information not specified",
                      null, null, null, null),
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null)));
    
    when(personService.getByAddress(anyString()))
            .thenReturn(List.of(childTest, parent1Test, parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
            .thenReturn(parent1RecordTest).thenThrow(
                  new ResourceNotFoundException("Medical record of firstNameC lastName not found"));

    // WHEN
    ChildAlertDto actualDto = alertsService.childAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(3)).getByName(anyString(), anyString());
  }
  
  @Test
  void getFireAlertTest() throws Exception {
    // GIVEN   
    FireAlertDto expectedDto = new FireAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11",
                      Collections.emptyList(), Collections.emptyList(), "000-000-0001", null),
              new PersonInfoDto("firstNameB", "lastName", null, "41",
                      List.of("med1", "med2"), Collections.emptyList(), "000-000-0001", null)),
              "1");
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest);
    when(fireStationService.getByAddress(anyString())).thenReturn(new FireStation(1, "address"));

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByAddress("address");
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
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest);
    when(fireStationService.getByAddress(anyString())).thenThrow(
              new ResourceNotFoundException("address mapping not found"));

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByAddress("address");
  }

  @Test
  void getfireAlertNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getByAddress(anyString())).thenThrow(
            new ResourceNotFoundException("No residents found living at address9"));
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.fireAlert("address9");
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents found living at address9");
    verify(personService, times(1)).getByAddress("address9");
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
  }
  
  @Test
  void getFireAlertMedicalRecordNotFoundTest() throws Exception {
    // GIVEN  
    Person parent2 = new Person("firstNameZ", "lastName", "address", "city", "", "", "");
    
    FireAlertDto expectedDto = new FireAlertDto(List.of(
            new PersonInfoDto("firstNameZ", "lastName", null, "Information not specified",
                    List.of("Information not specified"), List.of("Information not specified"),
                    "", null),
            new PersonInfoDto("firstNameA", "lastName", null, "11",
                    Collections.emptyList(), Collections.emptyList(), "000-000-0001", null)),
             "1");
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenThrow(
                  new ResourceNotFoundException("Medical record of firstNameZ lastName not found"));
    when(fireStationService.getByAddress(anyString())).thenReturn(new FireStation(1, "address"));

    // WHEN
    FireAlertDto actualDto = alertsService.fireAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByAddress("address");
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
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest).thenReturn(parent2RecordTest);
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto, floodHousehold2Dto));
    verify(personService, times(1)).getByAddress("address");
    verify(personService, times(1)).getByAddress("address2");
    verify(medicalRecordService, times(3)).getByName(anyString(), anyString());
    verify(fireStationService, times(2)).getByStation(anyInt());
  }
  
  @Test
  void getFloodAlertMappingNotFoundTest() throws Exception {
    // GIVEN   
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest).thenReturn(parent2RecordTest);
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenThrow(new ResourceNotFoundException("No addresses mapped for station 9 found"));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 9));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto));
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(2)).getByStation(anyInt());
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
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenReturn(List.of(parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest).thenThrow(
                  new ResourceNotFoundException("Medical record of firstNameZ lastName not found"));
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto, floodHousehold2Dto));
    verify(personService, times(1)).getByAddress("address");
    verify(personService, times(1)).getByAddress("address2");
    verify(medicalRecordService, times(3)).getByName(anyString(), anyString());
    verify(fireStationService, times(2)).getByStation(anyInt());
  }
  
  @Test
  void getFloodAlertNoResidentFoundForAnAddressTest() throws Exception {
    // GIVEN   
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
            new PersonInfoDto("firstNameA", "lastName", null, "11", Collections.emptyList(),
                    Collections.emptyList(), "000-000-0001", null),
            new PersonInfoDto("firstNameB", "lastName", null, "41", List.of("med1", "med2"),
                    Collections.emptyList(), "000-000-0001", null))); 
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test))
          .thenThrow(new ResourceNotFoundException("No residents found living at address2"));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest).thenReturn(parent2RecordTest);
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")))
          .thenReturn(List.of(new FireStation(2, "address2")));

    // WHEN
    List<FloodHouseholdDto> actualDto = alertsService.floodAlert(List.of(1, 2));

    // THEN
    assertThat(actualDto).usingRecursiveComparison()
            .isEqualTo(List.of(floodHouseholdDto));
    verify(personService, times(1)).getByAddress("address");
    verify(personService, times(1)).getByAddress("address2");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(2)).getByStation(anyInt());
  }
  
  @Test
  void getFloodAlertNoResidentFoundTest() throws Exception {
    // GIVEN   
    when(personService.getByAddress(anyString()))
          .thenThrow(new ResourceNotFoundException("No residents found living at address"));
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address9")))
          .thenThrow(new ResourceNotFoundException("No addresses mapped for station 10 found"));

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.floodAlert(List.of(9, 10));
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents covered found for stations [9, 10]");
    verify(personService, times(1)).getByAddress("address9");
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
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
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent1Test));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenReturn(parent1RecordTest);
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireStationCoverageDto actualDto = alertsService.fireStationCoverage(1);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByStation(1);
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
    
    when(personService.getByAddress(anyString()))
          .thenReturn(List.of(childTest, parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecordTest)
          .thenThrow(
            new ResourceNotFoundException("Medical record of firstNameZ lastName not found"));
    when(fireStationService.getByStation(anyInt()))
          .thenReturn(List.of(new FireStation(1, "address")));

    // WHEN
    FireStationCoverageDto actualDto = alertsService.fireStationCoverage(1);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(2)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByStation(1);
  }
  
  @Test
  void fireStationCoverageMappedAddressesNotFoundTest() throws Exception {
    // GIVEN   
    when(fireStationService.getByStation(anyInt()))
          .thenThrow(new ResourceNotFoundException("No addresses mapped for station 9 found"));
    
    // WHEN
    assertThatThrownBy(() -> {
      alertsService.fireStationCoverage(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No residents covered found for station 9");
    verify(personService, times(0)).getByAddress(anyString());
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
    verify(fireStationService, times(1)).getByStation(9);
  }
}
