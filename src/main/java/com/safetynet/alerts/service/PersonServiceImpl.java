package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class implementation handling CRUD operations for Person.
 */
@Service
public class PersonServiceImpl implements PersonService {

  @Autowired
  PersonRepository personRepository;
  
  @Override
  public List<Person> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Person getByName(String firstName, String lastName) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Person add(Person person) throws ResourceAlreadyExistsException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Person update(Person person) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    // TODO Auto-generated method stub

  }

}
