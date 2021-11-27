package com.safetynet.alerts.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.persistence.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("IntegrationTests")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "com.safetynet.alerts.jsonUrl=/testdata.json")
class AlertsIntegrationTest {

  @Autowired
  private DataLoader dataLoader;

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {
    dataLoader.load("/testdata.json");
  }

  @Test
  void postPersonIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/communityEmail?city=Test"))

            // THEN
            // Check response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0]", is("test1@email.com")));
  }

}