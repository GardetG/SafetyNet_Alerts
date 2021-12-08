package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FireStationCoverageDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.AlertsService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
 * Controller Class for SafetyNet Alerts URL.
 */
@Controller
@Validated
public class AlertsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsController.class);

  @Autowired
  AlertsService alertsService;

  /**
   * Handle HTTP GET request on /communityEmail and return the residents' email
   * list of the city.
   * 

   * @param city of the residents
   * @return HTTP 200 Response with a list of email
   * @throws ResourceNotFoundException when no residents' email found for the city
   */
  @GetMapping("/communityEmail")
  public ResponseEntity<List<String>> getCommunityEmail(
          @RequestParam @NotBlank(message = "City is mandatory") String city)
          throws ResourceNotFoundException {
            
    LOGGER.info("Request: Get community Email for {}", city);
    List<String> emailsList = alertsService.getCommunityEmail(city);

    LOGGER.info("Response: Community Email sent");
    return ResponseEntity.ok(emailsList);

  }

  /**
   * Handle HTTP GET request on /phoneAlert and return the residents' phone number
   * list of the city.
   * 

   * @param firestation id covering the residents
   * @return HTTP 200 Response with a list of phone numbers
   * @throws ResourceNotFoundException when no residents' phone numbers found for this station
   */
  @GetMapping("/phoneAlert")
  public ResponseEntity<List<String>> getPhoneAlert(
          @RequestParam @Range(min = 1, message = "Station Id must be greater than 0")
          int firestation)
          throws ResourceNotFoundException {
            
    LOGGER.info("Request: Get phone alert informations for station {}", firestation);
    List<String> emailsList = alertsService.getPhoneAlert(firestation);

    LOGGER.info("Response: phone alert information sent");
    return ResponseEntity.ok(emailsList);

  }
  
  /**
   * Handle HTTP GET request on /personInfo and return list of person informations with
   * address, age, and medical data.
   * 

   * @param firstName of the persons
   * @param lastName of the persons
   * @return HTTP 200 Response with a list persons with their informations
   * @throws ResourceNotFoundException when the person is not found
   */
  @GetMapping("/personInfo")
  public ResponseEntity<List<PersonInfoDto>> getPersonInfo(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get {} {} informations", firstName, lastName);
    List<PersonInfoDto> personInfo = alertsService.getPersonInfo(firstName, lastName);
    
    LOGGER.info("Response: persons informations sent");
    return ResponseEntity.ok(personInfo);

  }
  
  /**
   * Handle HTTP GET request on /childAlert and return child alert information with
   * a list of children of the household with their age and the list of others household members.
   * 

   * @param address of the household
   * @return HTTP 200 Response with child alert informations
   * @throws ResourceNotFoundException when no children found at this address
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
   * @throws ResourceNotFoundException when no residents found at this address
   */
  @GetMapping("/fire")
  public ResponseEntity<FireAlertDto> getFireAlert(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fire alert informations at {}", address);
    FireAlertDto fireAlertDto = alertsService.fireAlert(address);
    
    LOGGER.info("Response: fire alert informations sent");
    return ResponseEntity.ok(fireAlertDto);

  }
  
  /**
   * Handle HTTP GET request on /flood/stations and return flood alert information with
   * a list of the residents covered by the stations with their age and medical data,
   * grouped by address.
   * 

   * @param stations of the residents
   * @return HTTP 200 Response with flood alert informations
   * @throws ResourceNotFoundException when no resident covered found for these stations
   */
  @GetMapping("/flood/stations")
  public ResponseEntity<List<FloodHouseholdDto>> getfloodAlert(
          @RequestParam @NotEmpty(message = "Stations list is mandatory") List<Integer> stations)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get flood alert informations for stations {}", stations);
    List<FloodHouseholdDto> floodListDto = alertsService.floodAlert(stations);
    
    LOGGER.info("Response: Flood alert informations sent");
    return ResponseEntity.ok(floodListDto);

  }
  
  /**
   * Handle HTTP GET request on /firestation and return the residents covered by
   * the station with children and adults count.
   * 

   * @param stationNumber id covering the residents
   * @return HTTP 200 Response with firestation coverage informations.
   * @throws ResourceNotFoundException when no residents found for this station
   */
  @GetMapping("/firestation")
  public ResponseEntity<FireStationCoverageDto> getFireStation(
          @RequestParam @Range(min = 1, message = "Station Id must be greater than 0")
          int stationNumber)
          throws ResourceNotFoundException {
            
    LOGGER.info("Request: Get station {} residents covered informations", stationNumber);
    FireStationCoverageDto coverageDto = alertsService.fireStationCoverage(stationNumber);

    LOGGER.info("Response: residents covered informations sent");
    return ResponseEntity.ok(coverageDto);

  }
}
