package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.List;
import lombok.Setter;

/**
 * Repository Class implementation for Person.
 */
public class PersonRepositoryImpl implements PersonRepository {

  @Setter
  private List<Person> personsList;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Person findByName(String firstName, String lastName) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(Person person) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(Person person) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(Person person) {
    // TODO Auto-generated method stub
    return false;
  }

}
