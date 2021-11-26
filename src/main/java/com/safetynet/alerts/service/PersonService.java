package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
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
  List<Person> getAll();

  /**
   * Get a Person by its name.
   * 

   * @param firstName of the Person
   * @param lastName of the Person
   * @return Person
   * @throws ResourceNotFoundException when the Person doesn't exist
   */
  Person getByName(String firstName, String lastName) throws ResourceNotFoundException;
  
  /**
   * Add a Person if it doesn't already exist.
   * 

   * @param person to add
   * @return Person added
   * @throws ResourceAlreadyExistsException when Person already exists
   */
  Person add(@Valid Person person) throws ResourceAlreadyExistsException;

  /**
   * Update a Person if it already exist.
   * 

   * @param person to update
   * @return Person updated
   * @throws ResourceNotFoundException when the Person doesn't exist
   */
  Person update(@Valid Person person) throws ResourceNotFoundException;

  /**
   * Delete a Person by its name.
   * 

   * @param firstName of the Person to delete
   * @param lastName of the Person to delete
   * @throws ResourceNotFoundException when the Person doesn't exist
   */
  void delete(String firstName, String lastName) throws ResourceNotFoundException;

}
