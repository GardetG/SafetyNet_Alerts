package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller Class for managing persons.
 */
@Controller
public class PersonController {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

  @Autowired
  PersonService personService;
  
}
