package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.PersonInfoDto;
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

   * @param city of the residents
   * @return HTTP 200 Response with a list of emails
   * @throws ResourceNotFoundException when no residents' emails found for the city
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

   * @param firestation id covering the residents
   * @return HTTP 200 Response with a list of phone numbers
   * @throws ResourceNotFoundException when no residents' phone numbers found this station
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
  
  
  /**
   * Handle HTTP GET request on /personInfo and return list of person informations with
   * address, age, and medical data.
   * 

   * @param firstName of the person
   * @param lastName of the person
   * @return HTTP 200 Response with a list persons informations
   * @throws ResourceNotFoundException when the person is not found
   */
  @GetMapping("/personInfo")
  public ResponseEntity<List<PersonInfoDto>> getPersonInfo(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get persons info with parameters: {}, {}", firstName, lastName);
    List<PersonInfoDto> personInfo = alertsService.getPersonInfo(firstName, lastName);
    
    LOGGER.info("Response: person sent");
    return ResponseEntity.ok(personInfo);

  }
  
  /**
   * Handle HTTP GET request on /childAlert and return child alert information with
   * a list of children of the household with their age and the list of others household members.
   * 

   * @param address of the household
   * @return HTTP 200 Response with child alert informations
   * @throws ResourceNotFoundException when no resident found at this address
   */
  @GetMapping("/childAlert")
  public ResponseEntity<ChildAlertDto> getChildAlert(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get child alert information at {}", address);
    ChildAlertDto childAlertDto = alertsService.childAlert(address);
    
    LOGGER.info("Response: Child alert informations sent");
    return ResponseEntity.ok(childAlertDto);

  }
  
  /**
   * Handle HTTP GET request on /fire and return fire alert information with
   * a list of the residents with their age and medical data and the associated station.
   * 

   * @param address of the residents
   * @return HTTP 200 Response with fire alert informations
   * @throws ResourceNotFoundException when no resident found at this address
   */
  @GetMapping("/fire")
  public ResponseEntity<FireAlertDto> getFireAlert(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fire alert information at {}", address);
    FireAlertDto fireAlertDto = alertsService.fireAlert(address);
    
    LOGGER.info("Response: fire alert informations sent");
    return ResponseEntity.ok(fireAlertDto);

  }
  
}
