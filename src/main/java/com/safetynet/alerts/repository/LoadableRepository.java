package com.safetynet.alerts.repository;

import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for a loadable repository.
 */
@Repository
public interface LoadableRepository<T>  {

  void setupRepository(List<T> resourcesList);
  
}
