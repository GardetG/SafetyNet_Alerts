package com.safetynet.alerts.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.PersonService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PersonController.class)
@AutoConfigureRestDocs
class PersonControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PersonService personService;

  private PersonDto personTest;
  private PersonDto personTest2;

  @BeforeEach
  void setUp() throws Exception {
    personTest = new PersonDto("firstName", "lastName", "address", "city", "0001", "000.000.0001",
            "email@mail.fr");
    personTest2 = new PersonDto("firstName2", "lastName2", "address2", "city2", "0002",
            "000.000.0002", "email2@mail.fr");
  }

  @Test
  void getAllPersonsTest() throws Exception {
    // GIVEN
    when(personService.getAll()).thenReturn(List.of(personTest, personTest2));

    // WHEN
    mockMvc.perform(get("/persons"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("firstName")))
            .andExpect(jsonPath("$[1].firstName", is("firstName2")));
    verify(personService, times(1)).getAll();
  }

  @Test
  void getAllPersonsWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getAll()).thenReturn(Collections.emptyList());

    // WHEN
    mockMvc.perform(get("/persons"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    verify(personService, times(1)).getAll();
  }
   
  @Test
  void getPersonTest() throws Exception {
    // GIVEN
    when(personService.getByName(anyString(), anyString())).thenReturn(personTest);

    // WHEN
    mockMvc.perform(get("/persons/person?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstName")));
    verify(personService, times(1)).getByName("firstName", "lastName");
  }
  
  @Test
  void getPersonWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(personService.getByName(anyString(), anyString())).thenReturn(personTest);

    // WHEN
    mockMvc.perform(get("/persons/person?firstName= &lastName=LastName"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Firstname is mandatory")));
    verify(personService, times(0)).getByName(anyString(), anyString());
  }

  @Test
  void getPersonWhenNotFoundTest() throws Exception {
    // GIVEN
    when(personService.getByName(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException("Firstname LastName is not found"));

    // WHEN
    mockMvc.perform(get("/persons/person?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Firstname LastName is not found")));
    verify(personService, times(1)).getByName("firstName", "lastName");
  }

}
