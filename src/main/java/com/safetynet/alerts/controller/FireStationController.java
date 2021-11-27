package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.dto.FireStationMapper;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.FireStationService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing fireStation mapping.
 */
@Controller
@Validated
public class FireStationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FireStationController.class);

  @Autowired
  FireStationService fireStationService;

  /**
   * Handle HTTP GET request on fireStation mapping collection.
   * 

   * @return HTTP 200 Response with a list of all fireStation mapping
   */
  @GetMapping("/fireStations")
  public ResponseEntity<List<FireStationDto>> getAllFireStations() {
    
    LOGGER.info("Request: Get all fireStation mapping");
    List<FireStationDto> allFireStations = FireStationMapper.toDto(fireStationService.getAll());
    
    LOGGER.info("Response: List of all fireStation mapping sent");
    return ResponseEntity.ok(allFireStations);
    
  }

  /**
   * Handle HTTP GET request on a fireStation resource by its firstName and lastName.
   * 

   * @param id of the fireStation
   * @return HTTP 200 Response with the fireStation
   * @throws ResourceNotFoundException when for this fireStation mapping are not found
   */
  
  @GetMapping("/fireStations/{id}")
  public ResponseEntity<List<FireStationDto>> getFireStation(
          @PathVariable @Range(min = 1, message = "Station Id must be greater than 0") int id)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fireStation mapping of station{}", id);
    List<FireStationDto> fireStationMappings = FireStationMapper.toDto(
            fireStationService.getByStation(id));
    
    LOGGER.info("Response: fireStation mapping sent");
    return ResponseEntity.ok(fireStationMappings);
    
  }
  
  /**
   * Handle HTTP GET request on a fireStation mapping resource by an address.
   * 

   * @param address of the fireStation mapping
   * @return HTTP 200 Response with the fireStation mapping
   * @throws ResourceNotFoundException when this address mapping is not found
   */
  
  @GetMapping("/fireStations/fireStation")
  public ResponseEntity<FireStationDto> getFireStation(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fireStation mapping for {}", address);
    FireStationDto fireStation = FireStationMapper.toDto(
            fireStationService.getByAddress(address));
    
    LOGGER.info("Response: fireStation mapping sent");
    return ResponseEntity.ok(fireStation);
    
  }
  
  
}
