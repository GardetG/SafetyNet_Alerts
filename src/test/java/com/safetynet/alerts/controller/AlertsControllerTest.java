package com.safetynet.alerts.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.AlertsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AlertsController.class)
@AutoConfigureRestDocs
class AlertsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AlertsService alertsService;

  @Test
  void getCommunityEmailTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenReturn(List.of(
            "email@test.fr", "email2@test.fr"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city=city"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is("email@test.fr")))
            .andExpect(jsonPath("$[1]", is("email2@test.fr")))
            .andDo(document("GetCommunityEmail",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("city").description(
                    "Address of the fireStation mapping to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).getCommunityEmail("city");
  }
  
  @Test
  void getCommunityEmailNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenThrow(
            new ResourceNotFoundException("No residents for city found"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city=city"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("No residents for city found")))
            .andDo(document("GetCommunityEmailNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).getCommunityEmail("city");
  }
  
  @Test
  void getCommunityEmailInvalidTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenReturn(List.of(
            "email@test.fr", "email2@test.fr"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("City is mandatory")))
            .andDo(document("GetCommunityEmailInvalid",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(0)).getCommunityEmail(anyString());
  }
}
