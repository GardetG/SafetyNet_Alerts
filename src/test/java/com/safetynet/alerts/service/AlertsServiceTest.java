package com.safetynet.alerts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
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
  }
  
  @Test
  void getPhoneAlertPhoneNumbersNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address9")));
    when(personService.getByAddress(anyString())).thenThrow(
            new ResourceNotFoundException("No residents found living at address9"));

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPhoneAlert(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No phone numbers for resident covered by station 9 found");
  }
  
}
