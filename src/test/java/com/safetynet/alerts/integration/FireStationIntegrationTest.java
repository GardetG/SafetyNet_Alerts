package com.safetynet.alerts.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.persistence.DataLoader;
import com.safetynet.alerts.util.JsonParser;
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
class FireStationIntegrationTest {

  @Autowired
  private DataLoader dataLoader;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {
    dataLoader.load("/testdata.json");
  }

  @Test
  void postFireStationIntegrationTest() throws Exception {
    // GIVEN
    FireStationDto fireStationTest = new FireStationDto(1, "9 Test St");

    // WHEN
    mockMvc.perform(post("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            // Check response
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.address", is("9 Test St")))
            .andExpect(jsonPath("$.station", is(1)));
    // Check that a fireStation has been added
    mockMvc.perform(get("/fireStations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
  }

  @Test
  void putFireStationIntegrationTest() throws Exception {
    // GIVEN
    FireStationDto fireStationTest = new FireStationDto(9, "1 Test St");

    // WHEN
    mockMvc.perform(put("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            // Check response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address", is("1 Test St")))
            .andExpect(jsonPath("$.station", is(9)));
    // Check that we can retrieve the updated fireStation
    mockMvc.perform(get("/fireStations/fireStation?address=1 Test St"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].address", is("1 Test St")))
            .andExpect(jsonPath("$[0].station", is(9)));
  }

  @Test
  void deleteFireStationByAddressIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation?address=1 Test St"))

            // THEN
            // Check response
            .andExpect(status().isNoContent());
    // Check that a fireStation has been removed
    mockMvc.perform(get("/fireStations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void deleteFireStationByStationIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation/2"))

            // THEN
            // Check response
            .andExpect(status().isNoContent());
    // Check that a fireStation has been removed
    mockMvc.perform(get("/fireStations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
  }
  
}
