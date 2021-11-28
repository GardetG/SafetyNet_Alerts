package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.AlertsService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class SafetyNet Alerts URL.
 */
@Controller
@Validated
public class AlertsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsController.class);

  @Autowired
  AlertsService alertsService;

  /**
   * Handle HTTP GET request on /communityEmail and return the residents' emails
   * list of the city.
   * 

   * @return HTTP 200 Response with a list of emails
   */
  @GetMapping("/communityEmail")
  public ResponseEntity<List<String>> getCommunityEmail(
          @RequestParam @NotBlank(message = "City is mandatory") String city)
          throws ResourceNotFoundException {
            
    LOGGER.info("Request: Get community Emails for {}", city);
    List<String> emailsList = alertsService.getCommunityEmail(city);

    LOGGER.info("Response: Community Emails sent");
    return ResponseEntity.ok(emailsList);

  }

  /**
   * Handle HTTP GET request on /phoneAlert and return the residents' phone number
   * list of the city.
   * 

   * @return HTTP 200 Response with a list of phone numbers
   */
  @GetMapping("/phoneAlert")
  public ResponseEntity<List<String>> getCommunityEmail(
          @RequestParam @Range(min = 1, message = "Station Id must be greater than 0")
          int firestation)
          throws ResourceNotFoundException {
            
    LOGGER.info("Request: Get phone alert for station {}", firestation);
    List<String> emailsList = alertsService.getPhoneAlert(firestation);

    LOGGER.info("Response: phone alert sent");
    return ResponseEntity.ok(emailsList);

  }
  
}
