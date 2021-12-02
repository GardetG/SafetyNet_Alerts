package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling CRUD operations for Person.
 */
@Service
public interface PersonService {

  /**
   * Get list of all Person.
   * 

   * @return list of Person
   */
  List<PersonDto> getAll();

  /**
   * Get a list of Person by their city.
   * 

   * @param city of the Person
   * @return List of Person
   * @throws ResourceNotFoundException when no Person found for this city
   */
  List<PersonDto> getByCity(String city) throws ResourceNotFoundException;
  
  /**
   * Get a Person by its name.
   * 

   * @param address of the Person
   * @return List of Person
   * @throws ResourceNotFoundException when  no Person found for this address
   */
  List<PersonDto> getByAddress(String address) throws ResourceNotFoundException;
  
  /**
   * Get a Person by its name.
   * 

   * @param firstName of the Person
   * @param lastName of the Person
   * @return Person
   * @throws ResourceNotFoundException when the Person doesn't exists
   */
  PersonDto getByName(String firstName, String lastName) throws ResourceNotFoundException;
  
  /**
   * Add a Person if it doesn't already exists.
   * 

   * @param person to add
   * @return Person added
   * @throws ResourceAlreadyExistsException when Person already exists
   */
  PersonDto add(@Valid PersonDto person) throws ResourceAlreadyExistsException;

  /**
   * Update a Person if it already exists.
   * 

   * @param person to update
   * @return Person updated
   * @throws ResourceNotFoundException when the Person doesn't exists
   */
  PersonDto update(@Valid PersonDto person) throws ResourceNotFoundException;

  /**
   * Delete a Person by its name.
   * 

   * @param firstName of the Person to delete
   * @param lastName of the Person to delete
   * @throws ResourceNotFoundException when the Person doesn't exists
   */
  void delete(String firstName, String lastName) throws ResourceNotFoundException;

}
