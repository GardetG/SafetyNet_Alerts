package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for Person.
 */
@Repository
public interface PersonRepository {

  /**
   * Find all Person in repository.
   * 

   * @return List of all Person
   */
  List<Person> findAll();

  /**
   * Find all Person from a city.
   * 

   * @param city of Person
   * @return List of all Person from the city
   */
  List<Person> findByCity(String city);
  
  /**
   * Find all Person living at an address.
   * 

   * @param address of Person
   * @return List of all Person at this address
   */
  List<Person> findByAddress(String address);
  
  /**
   * Find a Person by its name.
   * 

   * @param firstName of the Person
   * @param lastName of the Person
   * @return Person
   */
  Optional<Person> findByName(String firstName, String lastName);
  
  /**
   * Add a Person to the repository if it doesn't already exist.
   * 

   * @param person to add
   * @return True is operation succeed
   */
  boolean add(Person person);

  /**
   * Update a Person in the repository if it exists.
   * 

   * @param person to update
   * @return True if operation succeed
   */
  boolean update(Person person);
  
  /**
   * Delete a Person in the repository if it exists.
   * 

   * @param person to delete
   * @return True if operation succeed
   */
  boolean delete(Person person);
  
}
