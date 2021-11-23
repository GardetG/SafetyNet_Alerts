package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for Person.
 */
@Repository
public interface PersonRepository {

  List<Person> findAll();

  Person findByName(String firstName, String lastName);
  
}
