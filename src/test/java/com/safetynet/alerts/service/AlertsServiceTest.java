package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.dto.ChildAlertDto;
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

  private Person personTest;
  private Person personTest2;
  private Person personTest3;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new Person("firstName", "lastName", "address", "city", "0001", "000-000-0001",
            "email@mail.fr");
    personTest2 = new Person("firstName2", "lastName2", "address", "city", "0002", "000-000-0001",
            "email2@mail.fr");
    personTest3 = new Person("firstName3", "lastName3", "address3", "city", "0003", "000-000-0002",
            "email2@mail.fr");
  }

  @Test
  void getCommunityEmailTest() throws Exception {
    // GIVEN
    when(personService.getByCity(anyString())).thenReturn(List.of(
            personTest, personTest2, personTest3));

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
            personTest, personNullEmail, personBlankEmail));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email@mail.fr"));
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
            .hasMessageContaining("No residents found for city9");
  }

  @Test
  void getPhoneAlertTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address2")));
    when(personService.getByAddress(anyString())).thenReturn(List.of(
            personTest, personTest2)).thenThrow(
                    new ResourceNotFoundException("No residents found living at address2"));

    // WHEN
    List<String> actualList = alertsService.getPhoneAlert(1);

    // THEN
    assertThat(actualList).isEqualTo(List.of("000-000-0001"));
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
            personTest, personNullEmail, personBlankEmail)).thenThrow(
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
            .hasMessageContaining("No addresses mapped for station 9 found");
    verify(fireStationService, times(1)).getByStation(9);
    verify(personService, times(0)).getByAddress(anyString());
  }
  
  @Test
  void getPersonInfoTest() throws Exception {
    // GIVEN
    Person person = new Person("firstName", "lastName", "address", "city", "", "", "");
    MedicalRecord medicalRecord = new MedicalRecord("firstName", "lastName",
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2"), Collections.emptyList());
    PersonInfoDto expectedDto = new PersonInfoDto("firstName", "lastName", "address", "41",
            List.of("med1", "med2"), Collections.emptyList(), null, null);
    when(personService.getByName(anyString(), anyString())).thenReturn(person);
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(medicalRecord);

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstName", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personService, times(1)).getByName("firstName", "lastName");
    verify(medicalRecordService, times(1)).getByName("firstName", "lastName");
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
    Person person = new Person("firstName", "lastName", "address", "city", "", "", "");
    PersonInfoDto expectedDto = new PersonInfoDto("firstName", "lastName", "address", 
            "Information not specified", List.of("Information not specified"), 
            List.of("Information not specified"), null, null);
    when(personService.getByName(anyString(), anyString())).thenReturn(person);
    when(medicalRecordService.getByName(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException("Medical record of firstName lastName not found"));

    // WHEN
    List<PersonInfoDto> actualList = alertsService.getPersonInfo("firstName", "lastName");

    // THEN
    assertThat(actualList).usingRecursiveComparison().isEqualTo(List.of(expectedDto));
    verify(personService, times(1)).getByName("firstName", "lastName");
    verify(medicalRecordService, times(1)).getByName("firstName", "lastName");
  }
  
  
  @Test
  void getChildAlertTest() throws Exception {
    // GIVEN
    Person child = new Person("firstNameA", "lastName", "address", "city", "", "", "");
    MedicalRecord childRecord = new MedicalRecord("firstNameA", "lastName",
            LocalDate.ofYearDay(2010, 1), Collections.emptyList(), Collections.emptyList());
    
    Person parent1 = new Person("firstNameB", "lastName", "address", "city", "", "", "");
    MedicalRecord parent1Record = new MedicalRecord("firstNameB", "lastName",
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2"), Collections.emptyList());
    
    Person parent2 = new Person("firstNameC", "lastName", "address", "city", "", "", "");
    MedicalRecord parent2Record = new MedicalRecord("firstNameC", "lastName",
            LocalDate.ofYearDay(1975, 1), List.of("med1", "med2"), Collections.emptyList());
    
    
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null),
              new PersonInfoDto("firstNameC", "lastName", null, null, null, null, null, null)));
    
    when(personService.getByAddress(anyString())).thenReturn(List.of(child, parent1, parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecord)
            .thenReturn(parent1Record).thenReturn(parent2Record);

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
    Person child = new Person("firstNameA", "lastName", "address", "city", "", "", "");
    MedicalRecord childRecord = new MedicalRecord("firstNameA", "lastName",
            LocalDate.ofYearDay(2010, 1), Collections.emptyList(), Collections.emptyList());
    
    Person parent1 = new Person("firstNameB", "lastName", "address", "city", "", "", "");
    MedicalRecord parent1Record = new MedicalRecord("firstNameB", "lastName",
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2"), Collections.emptyList());
    
    Person parent2 = new Person("firstNameC", "lastName", "address", "city", "", "", "");
    
    ChildAlertDto expectedDto = new ChildAlertDto(List.of(
              new PersonInfoDto("firstNameA", "lastName", null, "11", null, null, null, null)),
            List.of(
              new PersonInfoDto("firstNameB", "lastName", null, null, null, null, null, null),
              new PersonInfoDto("firstNameC", "lastName", null, "Information not specified",
                      null, null, null, null)));
    
    when(personService.getByAddress(anyString())).thenReturn(List.of(child, parent1, parent2));
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(childRecord)
            .thenReturn(parent1Record).thenThrow(
                  new ResourceNotFoundException("Medical record of firstNameC lastName not found"));

    // WHEN
    ChildAlertDto actualDto = alertsService.childAlert("address");

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(expectedDto);
    verify(personService, times(1)).getByAddress("address");
    verify(medicalRecordService, times(3)).getByName(anyString(), anyString());
  }
}
