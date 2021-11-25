package com.safetynet.alerts.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.PersonDto;
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
class PersonIntegrationTest {

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
    PersonDto personTest = new PersonDto("firstNameZ", "lastNameZ", "", "", "", "", "");

    // WHEN
    mockMvc.perform(post("/person")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(personTest)))

            // THEN
            // Check response
            .andExpect(status().isCreated()).andExpect(jsonPath("$.firstName", is("firstNameZ")))
            .andExpect(jsonPath("$.lastName", is("lastNameZ")));
    // Check that a person has been added
    mockMvc.perform(get("/persons"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
  }

  @Test
  void putPersonIntegrationTest() throws Exception {
    // GIVEN
    PersonDto personTest = new PersonDto("firstNameA", "lastNameA", "update", "", "", "", "");

    // WHEN
    mockMvc.perform(put("/person").contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(personTest)))

            // THEN
            // Check response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstNameA")))
            .andExpect(jsonPath("$.lastName", is("lastNameA")))
            .andExpect(jsonPath("$.address", is("update")));
    // Check that we can retrieve the updated person
    mockMvc.perform(get("/persons/person?firstName=firstNameA&lastName=lastNameA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstNameA")))
            .andExpect(jsonPath("$.lastName", is("lastNameA")))
            .andExpect(jsonPath("$.address", is("update")));
  }

  @Test
  void deletePersonIntegrationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/person?firstName=firstNameA&lastName=lastNameA"))

            // THEN
            // Check response
            .andExpect(status().isNoContent());
    // Check that a person has been removed
    mockMvc.perform(get("/persons"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
  }

}
