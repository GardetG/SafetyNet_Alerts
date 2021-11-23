package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.dto.PersonMapper;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.PersonService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    List<PersonDto> allPersons = PersonMapper.toDto(personService.getAll());
    
    LOGGER.info("Response: List of all persons sent");
    return ResponseEntity.ok(allPersons);
    
  }

  /**
   * Handle HTTP GET request on a person resource by its firstName and lastName.
   * 

   * @param firstName of the person
   * @param lastName  of the person
   * @return HTTP 200 Response with the person
   * @throws ResourceNotFoundException when the person is not found
   */
  
  @GetMapping("/persons/person")
  public ResponseEntity<PersonDto> getPerson(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get persons with parameters: {}, {}", firstName, lastName);
    PersonDto person = PersonMapper.toDto(personService.getByName(firstName, lastName));
    
    LOGGER.info("Response: person sent");
    return ResponseEntity.ok(person);
    
  }

  /**
   * Handle HTTP POST request for a person resource.
   * 

   * @param person to create
   * @return HTTP 201 Response with the person created
   * @throws ResourceAlreadyExistsException when the person already exists
   */
  @PostMapping("/person")
  public ResponseEntity<PersonDto> postPerson(@Valid @RequestBody PersonDto person) 
          throws ResourceAlreadyExistsException {

    LOGGER.info("Request: Create person {} {}", person.getFirstName(), person.getLastName());
    PersonDto createdPerson = PersonMapper.toDto(personService.add(PersonMapper.toModel(person)));

    URI uri = URI.create("/persons/person?firstName=" + createdPerson.getFirstName() + "&lastName="
            + createdPerson.getLastName());
    LOGGER.info("Response: person created");
    return ResponseEntity.created(uri).body(createdPerson);
  }
  
  /**
   * Handle HTTP PUT request on a person resource.
   * 

   * @param person to update
   * @return HTTP 200 Response with the person updated
   * @throws ResourceNotFoundException when the person to update is not found
   */
  @PutMapping("/person")
  public ResponseEntity<PersonDto> putPerson(@Valid @RequestBody PersonDto person) 
          throws ResourceNotFoundException {

    LOGGER.info("Request: Update person {} {}", person.getFirstName(), person.getLastName());
    PersonDto updatedPerson = PersonMapper.toDto(personService
            .update(PersonMapper.toModel(person)));
    
    LOGGER.info("Response: Person updated");
    return ResponseEntity.ok(updatedPerson);
  }
  
  /**
   * Handle HTTP DELETE request on a person resource.
   * 

   * @param firstName of person to delete
   * @param lastName of person to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException when the person to delete is not found
   */
  @DeleteMapping("/person")
  public ResponseEntity<Void> deletePerson(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {

    LOGGER.info("Request: Delete persons with parameters: {}, {}", firstName, lastName);
    personService.delete(firstName, lastName);
    
    LOGGER.info("Response: Person deleted");
    return ResponseEntity.noContent().build();

  }
  
}
