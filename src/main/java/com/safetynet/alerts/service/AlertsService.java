package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling SafetyNet Alerts requests.
 */
@Service
public interface AlertsService {

  /**
   * Return a list of all residents' email of the city without duplicate.
   * 

   * @param city of the residents
   * @return list of emails
   * @throws ResourceNotFoundException when no residents found for this city
   */
  List<String> getCommunityEmail(String city) throws ResourceNotFoundException;
  
}