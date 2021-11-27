package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.repository.PersonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class implementation handling SafetyNet Alerts requests.
 */
@Service
public class AlertsServiceImpl implements AlertsService {

  @Autowired
  PersonRepository personRepository;
  
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

}
