package com.safetynet.alerts.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.persistence.DataLoader;
import com.safetynet.alerts.util.JsonParser;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("IntegrationTests")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "com.safetynet.alerts.jsonUrl=/testdata.json")
class MedicalRecordIntegrationTest {

  @Autowired
  private DataLoader dataLoader;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {
    dataLoader.load("/testdata.json");
  }

  @Test
  void postMedicalRecordIntegrationTest() throws Exception {
    // GIVEN
    MedicalRecordDto medicalRecordTest = new MedicalRecordDto("firstNameZ", "lastNameZ", 
            LocalDate.ofYearDay(1980, 1), List.of("med1", "med2"), Collections.emptyList());

    // WHEN
    mockMvc.perform(post("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            // Check response
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("firstNameZ")))
            .andExpect(jsonPath("$.lastName", is("lastNameZ")))
            .andExpect(jsonPath("$.birthdate", is("01/01/1980")))
            .andExpect(jsonPath("$.medications", hasSize(2)))
            .andExpect(jsonPath("$.allergies", hasSize(0)));
    // Check that a medicalRecord has been added
    mockMvc.perform(get("/medicalRecords"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
  }

  @Test
  void putMedicalRecordIntegrationTest() throws Exception {
    // GIVEN
    MedicalRecordDto medicalRecordTest = new MedicalRecordDto("firstNameA", "lastNameA", 
            LocalDate.ofYearDay(2000, 1), Collections.emptyList(), List.of("allg1"));

    // WHEN
    mockMvc.perform(put("/medicalRecord").contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            // Check response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstNameA")))
            .andExpect(jsonPath("$.lastName", is("lastNameA")))
            .andExpect(jsonPath("$.birthdate", is("01/01/2000")))
            .andExpect(jsonPath("$.medications", hasSize(0)))
            .andExpect(jsonPath("$.allergies", hasSize(1)));
    // Check that we can retrieve the updated medicalRecord
    mockMvc.perform(get("/medicalRecords/medicalRecord?firstName=firstNameA&lastName=lastNameA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstNameA")))
            .andExpect(jsonPath("$.lastName", is("lastNameA")))
            .andExpect(jsonPath("$.birthdate", is("01/01/2000")))
            .andExpect(jsonPath("$.medications", hasSize(0)))
            .andExpect(jsonPath("$.allergies", hasSize(1)));
  }

  @Test
  void deleteMedicalRecordIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/medicalRecord?firstName=firstNameA&lastName=lastNameA"))

            // THEN
            // Check response
            .andExpect(status().isNoContent());
    // Check that a medicalRecord has been removed
    mockMvc.perform(get("/medicalRecords"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
  }

}
