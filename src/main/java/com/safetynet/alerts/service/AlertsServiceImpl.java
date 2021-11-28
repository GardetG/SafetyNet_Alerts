package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.PersonRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class implementation handling SafetyNet Alerts requests.
 */
@Service
public class AlertsServiceImpl implements AlertsService {

  @Autowired
  PersonRepository personRepository;
  
  @Autowired
  FireStationRepository fireStationRepository;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {
    List<String> emailsList = personRepository.findByCity(city).stream()
            .map(person -> { 
              return person.getEmail(); 
            }).distinct()
            .filter(email -> (email != null && !email.isBlank()))
            .collect(Collectors.toList());
    
    if (emailsList.isEmpty()) {
      String error = String.format("No residents for %s found", city);
      throw new ResourceNotFoundException(error);
    }
    
    return emailsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getPhoneAlert(int station) throws ResourceNotFoundException {
    List<String> addressesList = fireStationRepository.findByStation(station)
            .stream()
            .map(fireStation -> {
              return fireStation.getAddress();
            }).collect(Collectors.toList());
    
    if (addressesList.isEmpty()) {
      String error = String.format("No addresses mapped found for station %s", station);
      throw new ResourceNotFoundException(error);
    }
    
    List<String> phoneNumbersList = addressesList
            .stream()
            .flatMap(address -> {
              return personRepository.findByAddress(address).stream();
            }).map(person -> {
              return person.getPhone();
            }).distinct()
            .filter(phone -> (phone != null && !phone.isBlank()))
            .collect(Collectors.toList());
    
    if (phoneNumbersList.isEmpty()) {
      String error = String.format("No phone numbers for resident covered by station %s found",
              station);
      throw new ResourceNotFoundException(error);
    }
    
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
