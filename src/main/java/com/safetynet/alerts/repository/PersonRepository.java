package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.List;
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
   * Find a Person in the by its name.
   * 

   * @param firstName of the Person
   * @param lastName of the Person
   * @return Person
   */
  Person findByName(String firstName, String lastName);
  
  /**
   * Add a Person to the repository.
   * 

   * @param person to add
   * @return True is operation succeed
   */
  boolean add(Person person);

  /**
   * Update a Person in the repository.
   * 

   * @param person to update
   * @return True if operation succeed
   */
  boolean update(Person person);
  
  /**
   * Delete a Person in the repository.
   * 

   * @param person to delete
   * @return True if operation succeed
   */
  boolean delete(Person person);
  
}
