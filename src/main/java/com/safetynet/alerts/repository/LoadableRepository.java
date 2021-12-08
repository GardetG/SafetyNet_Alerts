package com.safetynet.alerts.repository;

import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for setting up a repository with initial data.
 */
@Repository
public interface LoadableRepository<T>  {

  /**
   * Set up the repository with the list of entities in parameter.
   * 

   * @param resourcesList to put in the repository
   */
  void setupRepository(List<T> resourcesList);
  
}
