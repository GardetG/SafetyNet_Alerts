package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for Person.
 */
@Repository
public class PersonRepositoryImpl implements LoadableRepository<Person>, PersonRepository {

  private List<Person> personsList = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> findAll() {
    return personsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> findByCity(String city) {
    return personsList.stream()
            .filter(person -> (person.getCity().equals(city)))
            .collect(Collectors.toList());
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> findByAddress(String address) {
    return personsList.stream()
            .filter(person -> (person.getAddress().equals(address)))
            .collect(Collectors.toList());
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<Person> findByName(String firstName, String lastName) {
    return personsList.stream()
            .filter(person -> (person.getFirstName().equals(firstName)
            && (person.getLastName().equals(lastName))))
            .findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(Person person) {
    Optional<Person> existingPerson = findByName(person.getFirstName(), person.getLastName());
    if (existingPerson.isPresent()) {
      return false;
    }
    return personsList.add(person);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(Person person) {
    Optional<Person> existingPerson = findByName(person.getFirstName(), person.getLastName());
    
    if (existingPerson.isEmpty()) {
      return false;
    }
    
    int index = personsList.indexOf(existingPerson.get());
    personsList.set(index, person);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(Person person) {
    return personsList.remove(person);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupRepository(List<Person> resourcesList) {
    personsList = resourcesList;
  }

}
