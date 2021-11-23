package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service Class implementation handling CRUD operations for Person.
 */
@Service
@Validated
public class PersonServiceImpl implements PersonService {

  @Autowired
  PersonRepository personRepository;

  @Override
  public List<Person> getAll() {
    return personRepository.findAll();
  }

  @Override
  public Person getByName(String firstName, String lastName) throws ResourceNotFoundException {
    Person person = personRepository.findByName(firstName, lastName);
    if (person == null) {
      String error = String.format("%s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }
    return person;
  }

  @Override
  public Person add(@Valid Person person) throws ResourceAlreadyExistsException {
    Person existingPerson = personRepository.findByName(person.getFirstName(),
            person.getLastName());

    if (existingPerson != null) {
      String error = String.format("%s %s already exists", person.getFirstName(),
              person.getLastName());
      throw new ResourceAlreadyExistsException(error);
    }

    personRepository.add(person);
    return personRepository.findByName(person.getFirstName(), person.getLastName());
  }

  @Override
  public Person update(@Valid Person person) throws ResourceNotFoundException {
    Person existingPerson = personRepository.findByName(person.getFirstName(),
            person.getLastName());
    if (existingPerson == null) {
      String error = String.format("%s %s not found", person.getFirstName(), person.getLastName());
      throw new ResourceNotFoundException(error);
    }

    personRepository.update(person);
    return personRepository.findByName(person.getFirstName(), person.getLastName());
  }

  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    // TODO Auto-generated method stub

  }

}
