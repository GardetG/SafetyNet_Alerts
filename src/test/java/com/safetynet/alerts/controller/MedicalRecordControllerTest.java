package com.safetynet.alerts.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import com.safetynet.alerts.util.JsonParser;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MedicalRecordController.class)
@AutoConfigureRestDocs
class MedicalRecordControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MedicalRecordService medicalRecordService;

  private MedicalRecord medicalRecordTest;
  private MedicalRecord medicalRecordTest2;

  @BeforeEach
  void setUp() throws Exception {
    medicalRecordTest = new MedicalRecord("firstName", "lastName", LocalDate.ofYearDay(1980, 1),
            List.of("med1", "med2"), Collections.emptyList());
    medicalRecordTest2 = new MedicalRecord("firstName2", "lastName2", LocalDate.ofYearDay(2000, 1),
            Collections.emptyList(), List.of("allg1"));
  }

  @Test
  void getAllMedicalRecordsTest() throws Exception {
    // GIVEN
    when(medicalRecordService.getAll()).thenReturn(List.of(medicalRecordTest, medicalRecordTest2));

    // WHEN
    mockMvc.perform(get("/medicalRecords"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].firstName", is("firstName")))
            .andExpect(jsonPath("$[1].firstName", is("firstName2")))
            .andDo(document("getAllMedicalRecord",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(1)).getAll();
  }

  @Test
  void getAllMedicalRecordsWhenNotFoundTest() throws Exception {
    // GIVEN
    when(medicalRecordService.getAll()).thenReturn(Collections.emptyList());

    // WHEN
    mockMvc.perform(get("/medicalRecords"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    verify(medicalRecordService, times(1)).getAll();
  }
   
  @Test
  void getMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(medicalRecordTest);

    // WHEN
    mockMvc.perform(get("/medicalRecords/medicalRecord?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstName")))
            .andDo(document("getMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("firstName").description(
                    "Firstname of the medical record to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional(),
                            parameterWithName("lastName").description(
                    "LastName of the medical record to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(medicalRecordService, times(1)).getByName("firstName", "lastName");
  }
  
  @Test
  void getMedicalRecordWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(medicalRecordService.getByName(anyString(), anyString())).thenReturn(medicalRecordTest);

    // WHEN
    mockMvc.perform(get("/medicalRecords/medicalRecord?firstName= &lastName=LastName"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Firstname is mandatory")))
            .andDo(document("getInvalidMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(0)).getByName(anyString(), anyString());
  }

  @Test
  void getMedicalRecordWhenNotFoundTest() throws Exception {
    // GIVEN
    when(medicalRecordService.getByName(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException("Medical record of firstname lastName not found"));

    // WHEN
    mockMvc.perform(get("/medicalRecords/medicalRecord?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Medical record of firstname lastName not found")))
            .andDo(document("getNotFoundMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(1)).getByName("firstName", "lastName");
  }
  

  @Test
  void postMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordService.add(any(MedicalRecord.class))).thenReturn(medicalRecordTest);

    // WHEN
    mockMvc.perform(post("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName", is("firstName")))
            .andExpect(jsonPath("$.lastName", is("lastName")))
            .andExpect(jsonPath("$.birthdate", is("01/01/1980")))
            .andDo(document("postMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("firstName")
                            .description("The first name of the person."
                                    + " This parameter *must not be blank*."),
                        fieldWithPath("lastName")
                            .description("The last name of the person. "
                                    + "This parameter *must not be blank*."),
                        fieldWithPath("birthdate")
                            .description("The birthdate of the person."),
                        fieldWithPath("medications")
                            .description("The list of all medications and their dosage taken"
                                    + " by this person."),
                        fieldWithPath("allergies")
                            .description("The list of all allergies of this person."))));
    verify(medicalRecordService, times(1)).add(medicalRecordTest);
  }

  @Test
  void postAlreadyExistingMedicalRecordTest() throws Exception {
    // GIVEN
    String error = String.format("Medical record of %s %s already exists", 
            medicalRecordTest.getFirstName(),
            medicalRecordTest.getLastName());
    when(medicalRecordService.add(any(MedicalRecord.class))).thenThrow(
            new ResourceAlreadyExistsException(error));

    // WHEN
    mockMvc.perform(post("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$", is(error)))
            .andDo(document("postConflictMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(1)).add(medicalRecordTest);
  }

  @Test
  void postInvalidMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecordDto invalidMedicalRecord = new MedicalRecordDto("", "lastName1", 
            LocalDate.ofYearDay(1980, 1), Collections.emptyList(), Collections.emptyList());

    // WHEN
    mockMvc.perform(post("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidMedicalRecord)))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.firstName", is("Firstname is mandatory")))
            .andDo(document("postInvalidMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(0)).add(any(MedicalRecord.class));
  }
  
  @Test
  void putMedicalRecordTest() throws Exception {
    // GIVEN
    when(medicalRecordService.update(any(MedicalRecord.class))).thenReturn(medicalRecordTest);

    // WHEN
    mockMvc.perform(put("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("firstName")))
            .andExpect(jsonPath("$.lastName", is("lastName")))
            .andExpect(jsonPath("$.birthdate", is("01/01/1980")))
            .andDo(document("putMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("firstName")
                            .description("The first name of the person."
                                    + " This parameter *must not be blank*."),
                        fieldWithPath("lastName")
                            .description("The last name of the person. "
                                    + "This parameter *must not be blank*."),
                        fieldWithPath("birthdate")
                            .description("The birthdate of the medicalRecord."),
                        fieldWithPath("medications")
                            .description("The list of all medications and their dosage taken"
                                    + " by this person."),
                        fieldWithPath("allergies")
                            .description("The list of all allergies of this person."))));
    verify(medicalRecordService, times(1)).update(medicalRecordTest);
  }

  @Test
  void putNotFoundMedicalRecordTest() throws Exception {
    // GIVEN
    String error = String.format("Medical record of %s %s not found", 
            medicalRecordTest.getFirstName(),
            medicalRecordTest.getLastName());
    when(medicalRecordService.update(any(MedicalRecord.class))).thenThrow(
            new ResourceNotFoundException(error));

    // WHEN
    mockMvc.perform(put("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(medicalRecordTest)))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Medical record of firstName lastName not found")))
            .andDo(document("putNotFoundMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(1)).update(medicalRecordTest);
  }

  @Test
  void putInvalidMedicalRecordTest() throws Exception {
    // GIVEN
    MedicalRecordDto invalidMedicalRecord = new MedicalRecordDto("", "lastName1", 
            LocalDate.ofYearDay(1980, 1), Collections.emptyList(), Collections.emptyList());

    // WHEN
    mockMvc.perform(put("/medicalRecord")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidMedicalRecord)))

            // THEN
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.firstName", is("Firstname is mandatory")))
            .andDo(document("putInvalidMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
  }
  
  @Test
  void deleteMedicalRecordTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/medicalRecord?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isNoContent())
            .andDo(document("deleteMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("firstName").description(
                    "Firstname of the medical record to delete. "
                    + "This parameter *must not be blank*.")
                            .optional(),
                            parameterWithName("lastName").description(
                    "LastName of the medical record to delete. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(medicalRecordService, times(1)).delete("firstName", "lastName");
  }

  @Test
  void deleteNotFoundMedicalRecordTest() throws Exception {
    // GIVEN
    String error = String.format("Medical record of %s %s not found", 
            medicalRecordTest.getFirstName(),
            medicalRecordTest.getLastName());
    doThrow(new ResourceNotFoundException(error)).when(medicalRecordService)
            .delete(anyString(), anyString());


    // WHEN
    mockMvc.perform(delete("/medicalRecord?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Medical record of firstName lastName not found")))
            .andDo(document("deleteNotFoundMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(1)).delete("firstName", "lastName");
  }
  
  @Test
  void deleteMedicalRecordWithInvalidArgumentsTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/medicalRecord?firstName= &lastName=LastName"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Firstname is mandatory")))
            .andDo(document("deleteInvalidMedicalRecord",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(medicalRecordService, times(0)).delete(anyString(), anyString());
  }
}
