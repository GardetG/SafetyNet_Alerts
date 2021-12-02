package com.safetynet.alerts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import com.safetynet.alerts.util.FireStationMapper;
import com.safetynet.alerts.util.JsonParser;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("UnitTests")
@WebMvcTest(FireStationController.class)
@AutoConfigureRestDocs
class FireStationControllerTest {

  @Autowired
  private MockMvc mockMvc;
  
  @Captor
  ArgumentCaptor<FireStationDto> fireStationCaptor;

  @MockBean
  private FireStationService fireStationService;

  private FireStationDto fireStationTest;
  private FireStationDto fireStationTest2;
  private FireStationDto fireStationTest3;

  @BeforeEach
  void setUp() throws Exception {
    fireStationTest = new FireStationDto(1, "address");
    fireStationTest2 = new FireStationDto(1, "address2");
    fireStationTest3 = new FireStationDto(2, "address3");
  }

  @Test
  void getAllFireStationsTest() throws Exception {
    // GIVEN
    when(fireStationService.getAll()).thenReturn(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    mockMvc.perform(get("/fireStations"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].station", is(1)))
            .andExpect(jsonPath("$[1].station", is(1)))
            .andExpect(jsonPath("$[2].station", is(2)))
            .andDo(document("getAllFireStation",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getAll();
  }

  @Test
  void getAllFireStationsWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getAll()).thenReturn(Collections.emptyList());

    // WHEN
    mockMvc.perform(get("/fireStations"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    verify(fireStationService, times(1)).getAll();
  }
   
  @Test
  void getFireStationByStationTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            fireStationTest, fireStationTest2));

    // WHEN
    mockMvc.perform(get("/fireStations/1"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].address", is("address")))
            .andExpect(jsonPath("$[1].address", is("address2")))
            .andDo(document("getFireStationById",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("id").description(
                    "Station Id of the fireStation mapping to retrieve. "
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).getByStation(1);
  }
  
  @Test
  void getFireStationWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            fireStationTest, fireStationTest2));

    // WHEN
    mockMvc.perform(get("/fireStations/0"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Station Id must be greater than 0")));
    verify(fireStationService, times(0)).getByStation(1);
  }

  @Test
  void getFireStationWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenThrow(
            new ResourceNotFoundException("Station 9 mapping not found"));

    // WHEN
    mockMvc.perform(get("/fireStations/9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Station 9 mapping not found")))
            .andDo(document("getNotFoundFireStationById",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getByStation(9);
  }
  
  @Test
  void getFireStationByAddressTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address=address"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address", is("address")))
            .andDo(document("getFireStationByAddress",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("address").description(
                    "Address of the fireStation mapping to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).getByAddress("address");
  }
  
  @Test
  void getFireStationByAddressWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Address is mandatory")));
    verify(fireStationService, times(0)).getByAddress(anyString());
  }

  @Test
  void getFireStationByAddressWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenThrow(
            new ResourceNotFoundException("Address9 mapping not found"));

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address=address9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Address9 mapping not found")))
            .andDo(document("getNotFoundFireStationByAddress",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getByAddress("address9");
  }
  
  @Test
  void postFireStationTest() throws Exception {
    // GIVEN
    when(fireStationService.add(any(FireStationDto.class))).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(post("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.address", is("address")))
            .andExpect(jsonPath("$.station", is(1)))
            .andDo(document("postFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("address")
                            .description("The address of the mapping."
                                    + " This parameter *must not be blank*."),
                        fieldWithPath("station")
                            .description("The fireStation of the mapping. "
                                    + "This parameter *must be greater than 0*."))));
    verify(fireStationService, times(1)).add(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue()).usingRecursiveComparison().isEqualTo(fireStationTest);
  }

  @Test
  void postAlreadyExistingFireStationTest() throws Exception {
    // GIVEN
    String error = String.format("%s mapping for station %s already exists", 
            fireStationTest.getAddress(),
            fireStationTest.getStation());
    when(fireStationService.add(any(FireStationDto.class))).thenThrow(
            new ResourceAlreadyExistsException(error));

    // WHEN
    mockMvc.perform(post("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$", is(error)))
            .andDo(document("postConflictFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).add(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue()).usingRecursiveComparison().isEqualTo(fireStationTest);
  }

  @Test
  void postInvalidFireStationTest() throws Exception {
    // GIVEN
    FireStationDto invalidFireStation = new FireStationDto(1, "");

    // WHEN
    mockMvc.perform(post("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidFireStation)))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.address", is("Address is mandatory")));
    verify(fireStationService, times(0)).add(any(FireStationDto.class));
  }
  
  @Test
  void putFireStationTest() throws Exception {
    // GIVEN
    when(fireStationService.update(any(FireStationDto.class))).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(put("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address", is("address")))
            .andExpect(jsonPath("$.station", is(1)))
            .andDo(document("putFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestFields(
                            fieldWithPath("address")
                                .description("The address of the mapping."
                                        + " This parameter *must not be blank*."),
                            fieldWithPath("station")
                                .description("The fireStation of the mapping. "
                                        + "This parameter *must be greater than 0*."))));
    verify(fireStationService, times(1)).update(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue()).usingRecursiveComparison().isEqualTo(fireStationTest);
  }

  @Test
  void putNotFoundFireStationTest() throws Exception {
    // GIVEN
    String error = String.format("%s mapping not found", 
            fireStationTest.getAddress());
    when(fireStationService.update(any(FireStationDto.class))).thenThrow(
            new ResourceNotFoundException(error));

    // WHEN
    mockMvc.perform(put("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(fireStationTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("address mapping not found")))
            .andDo(document("putNotFoundFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).update(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue()).usingRecursiveComparison().isEqualTo(fireStationTest);
  }

  @Test
  void putInvalidFireStationTest() throws Exception {
    // GIVEN
    FireStationDto invalidFireStation = new FireStationDto(1, "");

    // WHEN
    mockMvc.perform(put("/fireStation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidFireStation)))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.address", is("Address is mandatory")));
  }
  
  @Test
  void deleteFireStationTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation/1"))

            // THEN
            .andExpect(status().isNoContent())
            .andDo(document("deleteFireStationById",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("id").description(
                    "Station Id of the fireStation mapping to retrieve. "
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).deleteByStation(1);
  }

  @Test
  void deleteNotFoundFireStationTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("Station 9 mapping not found"))
            .when(fireStationService)
            .deleteByStation(anyInt());


    // WHEN
    mockMvc.perform(delete("/fireStation/9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Station 9 mapping not found")))
            .andDo(document("deleteNotFoundFireStationById",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).deleteByStation(9);
  }
  
  @Test
  void deleteFireStationWithInvalidArgumentsTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation/0"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Station Id must be greater than 0")));
    verify(fireStationService, times(0)).deleteByStation(anyInt());
  }
  
  @Test
  void deleteFireStationByAddressTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation?address=address"))

            // THEN
            .andExpect(status().isNoContent())
            .andDo(document("deleteFireStationByAddress",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("address").description(
                    "Address of the fireStation mapping to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).deleteByAddress("address");
  }

  @Test
  void deleteNotFoundFireStationByAddressTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("address9 mapping not found"))
            .when(fireStationService)
            .deleteByAddress(anyString());


    // WHEN
    mockMvc.perform(delete("/fireStation?address=address9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("address9 mapping not found")))
            .andDo(document("deleteNotFoundFireStationByAddress",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).deleteByAddress("address9");
  }
  
  @Test
  void deleteFireStationByAddressWithInvalidArgumentsTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/fireStation?address="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Address is mandatory")));
    verify(fireStationService, times(0)).deleteByAddress(anyString());
  }
  
}
