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

  List<Person> getAll();

  Person getByName(String firstName, String lastName) throws ResourceNotFoundException;
  
  Person add(@Valid Person person) throws ResourceAlreadyExistsException;

  Person update(@Valid Person person) throws ResourceNotFoundException;

  void delete(String firstName, String lastName) throws ResourceNotFoundException;

}
