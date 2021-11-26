package com.safetynet.alerts.controller;

import com.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller Class for managing medical records.
 */
@Controller
public class MedicalRecordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordController.class);

  @Autowired
  MedicalRecordService medicalRecordService;
}
