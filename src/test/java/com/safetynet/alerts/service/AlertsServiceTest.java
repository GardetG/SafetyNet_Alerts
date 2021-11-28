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
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
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
  PersonRepository personRepository;
  
  @MockBean
  FireStationRepository fireStationRepository;

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
    when(personRepository.findByCity(anyString())).thenReturn(List.of(
            personTest, personTest2, personTest3));

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
            personTest, personNullEmail, personBlankEmail));

    // WHEN
    List<String> actualList = alertsService.getCommunityEmail("city");

    // THEN
    assertThat(actualList).isEqualTo(List.of("email@mail.fr"));
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
            .hasMessageContaining("No residents for city9 found");
  }

  @Test
  void getPhoneAlertTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address"), new FireStation(1, "address2")));
    when(personRepository.findByAddress(anyString())).thenReturn(List.of(
            personTest, personTest2)).thenReturn(Collections.emptyList());

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
            new FireStation(1, "address"), new FireStation(1, "address2")));
    when(personRepository.findByAddress(anyString())).thenReturn(List.of(
            personTest, personNullEmail, personBlankEmail)).thenReturn(Collections.emptyList());

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
            .hasMessageContaining("No addresses mapped found for station 9");
  }
  
  @Test
  void getPhoneAlertPhoneNumbersNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationRepository.findByStation(anyInt())).thenReturn(List.of(
            new FireStation(1, "address9")));
    when(personRepository.findByAddress(anyString())).thenReturn(Collections.emptyList());

    // WHEN
    assertThatThrownBy(() -> {
      alertsService.getPhoneAlert(9);
    })

            // THEN
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No phone numbers for resident covered by station 9 found");
  }
  
}
