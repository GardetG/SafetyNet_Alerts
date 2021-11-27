package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.FireStationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

/**
 * Controller Class for managing fireStation mapping.
 */
@Controller
@Validated
public class FireStationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FireStationController.class);

  @Autowired
  FireStationService fireStationService;

}
