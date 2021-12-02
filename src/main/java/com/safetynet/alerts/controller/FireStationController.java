package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.FireStationService;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    List<FireStationDto> allFireStations = fireStationService.getAll();
    
    LOGGER.info("Response: List of all fireStation mapping sent");
    return ResponseEntity.ok(allFireStations);
    
  }

  /**
   * Handle HTTP GET request on a fireStation mapping resource by id.
   * 

   * @param id of the fireStation
   * @return HTTP 200 Response with the fireStation
   * @throws ResourceNotFoundException when mapping are not found for this fireStation
   */
  
  @GetMapping("/fireStations/{id}")
  public ResponseEntity<List<FireStationDto>> getFireStationById(
          @PathVariable @Range(min = 1, message = "Station Id must be greater than 0") int id)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fireStation mapping of station{}", id);
    List<FireStationDto> fireStationMappings = fireStationService.getByStation(id);
    
    LOGGER.info("Response: fireStation mapping sent");
    return ResponseEntity.ok(fireStationMappings);
    
  }
  
  /**
   * Handle HTTP GET request on a fireStation mapping resource by an address.
   * 

   * @param address of the fireStation mapping
   * @return HTTP 200 Response with the fireStation mapping
   * @throws ResourceNotFoundException when mapping is not found for this address
   */
  
  @GetMapping("/fireStations/fireStation")
  public ResponseEntity<FireStationDto> getFireStationByAddress(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get fireStation mapping for {}", address);
    FireStationDto fireStation = fireStationService.getByAddress(address);
    
    LOGGER.info("Response: fireStation mapping sent");
    return ResponseEntity.ok(fireStation);
    
  }
  
  /**
   * Handle HTTP POST request for a fireStation mapping resource.
   * 

   * @param fireStation mapping to create
   * @return HTTP 201 Response with the fireStation mapping created
   * @throws ResourceAlreadyExistsException when the fireStation mapping already exists
   */
  @PostMapping("/fireStation")
  public ResponseEntity<FireStationDto> postFireStation(
          @Valid @RequestBody FireStationDto fireStation) 
          throws ResourceAlreadyExistsException {

    LOGGER.info("Request: Create {} mapping to station {}", 
            fireStation.getAddress(), fireStation.getStation());
    FireStationDto createdFireStation = fireStationService.add(fireStation);

    String address = URLEncoder.encode(createdFireStation.getAddress(), 
            java.nio.charset.StandardCharsets.UTF_8);
    URI uri = URI.create("/fireStations/fireStation?address=" + address);
    LOGGER.info("Response: fireStation mapping created");
    return ResponseEntity.created(uri).body(createdFireStation);
  }
  
  /**
   * Handle HTTP PUT request on a fireStation mapping resource.
   * 

   * @param fireStation mapping to update
   * @return HTTP 200 Response with the fireStation mapping updated
   * @throws ResourceNotFoundException when the fireStation mapping to update is not found
   */
  @PutMapping("/fireStation")
  public ResponseEntity<FireStationDto> putFireStation(
          @Valid @RequestBody FireStationDto fireStation) 
          throws ResourceNotFoundException {

    LOGGER.info("Request: Update {} mapping to station {}", 
            fireStation.getAddress(), fireStation.getStation());
    FireStationDto updatedFireStation = fireStationService.update(fireStation);
    
    LOGGER.info("Response: FireStation mapping updated");
    return ResponseEntity.ok(updatedFireStation);
  }
  
  /**
   * Handle HTTP DELETE request on a fireStation mapping resource by id.
   * 

   * @param id of fireStation mapping to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException when mapping are not found for this fireStation
   */
  @DeleteMapping("/fireStation/{id}")
  public ResponseEntity<Void> deleteFireStationById(
          @PathVariable @Range(min = 1, message = "Station Id must be greater than 0") int id)
          throws ResourceNotFoundException {

    LOGGER.info("Request: Delete fireStation mapping for station {}", id);
    fireStationService.deleteByStation(id);
    
    LOGGER.info("Response: FireStation mapping deleted");
    return ResponseEntity.noContent().build();

  }
  
  /**
   * Handle HTTP DELETE request on a fireStation mapping resource by address.
   * 

   * @param address of fireStation mapping to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException when mapping is not found for this address
   */
  @DeleteMapping("/fireStation")
  public ResponseEntity<Void> deleteFireStationbyAddress(
          @RequestParam @NotBlank(message = "Address is mandatory") String address)
          throws ResourceNotFoundException {

    LOGGER.info("Request: Delete fireStations mapping for {}", address);
    fireStationService.deleteByAddress(address);
    
    LOGGER.info("Response: FireStation mapping deleted");
    return ResponseEntity.noContent().build();

  }
}
