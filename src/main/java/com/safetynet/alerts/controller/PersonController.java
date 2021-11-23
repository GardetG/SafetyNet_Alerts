package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.PersonService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing persons.
 */
@Controller
@Validated
public class PersonController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

  @Autowired
  PersonService personService;

  /**
   * Handle HTTP GET request on persons collection.
   * 

   * @return HTTP 200 Response with a list of all persons
   */
  @GetMapping("/persons")
  public ResponseEntity<List<PersonDto>> getAllPersons() {
    
    LOGGER.info("Request: Get all persons");
    List<PersonDto> allPersons = personService.getAll();
    LOGGER.info("Response: List of all persons sent");
    return ResponseEntity.ok(allPersons);
    
  }

  /**
   * Handle HTTP GET request on a person resource by its firstName and lastName.
   * 

   * @param firstName of the person
   * @param lastName  of the person
   * @return HTTP 200 Response with the person
   */
  @GetMapping("/persons/person")
  public ResponseEntity<PersonDto> getPerson(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get persons with parameter: {}, {}", firstName, lastName);
    PersonDto person = personService.getByName(firstName, lastName);
    LOGGER.info("Response: person sent");
    return ResponseEntity.ok(person);
    
  }

}
