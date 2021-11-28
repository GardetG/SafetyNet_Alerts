package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceNotFoundException;
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
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {
    List<String> emailsList = personRepository.findByCity(city).stream()
            .map(person -> { 
              return person.getEmail(); 
            }).distinct()
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
    // TODO Auto-generated method stub
    return null;
  }

}
