package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.AlertsService;
import com.safetynet.alerts.service.FireStationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller Class SafetyNet Alerts URL.
 */
@Controller
public class AlertsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsController.class);

  @Autowired
  AlertsService alertsService;
  
}
