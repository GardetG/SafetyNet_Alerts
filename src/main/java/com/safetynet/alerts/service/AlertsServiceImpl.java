package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.AlertsController;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class implementation handling SafetyNet Alerts requests.
 */
@Service
public class AlertsServiceImpl implements AlertsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsServiceImpl.class);
  
  @Autowired
  PersonService personService;
  
  @Autowired
  FireStationService fireStationService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {
    List<String> emailsList = personService.getByCity(city).stream()
            .map(person -> { 
              return person.getEmail(); 
            }).distinct()
            .filter(email -> (email != null && !email.isBlank()))
            .collect(Collectors.toList());
    
    return emailsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getPhoneAlert(int station) throws ResourceNotFoundException {
    List<String> addressesList = fireStationService.getByStation(station)
            .stream()
            .map(fireStation -> {
              return fireStation.getAddress();
            }).collect(Collectors.toList());
    
    List<String> phoneNumbersList = addressesList
            .stream()
            .flatMap(address -> {
              List<Person> persons = new ArrayList<>();
              try {
                persons = personService.getByAddress(address);
              } catch (ResourceNotFoundException ex) {
                LOGGER.warn(ex.getMessage());
              }
              return persons.stream();
            }).map(person -> {
              return person.getPhone();
            }).distinct()
            .filter(phone -> (phone != null && !phone.isBlank()))
            .collect(Collectors.toList());
    
    return phoneNumbersList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PersonInfoDto> getPersonInfo(String firstName, String lastName)
          throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

}
