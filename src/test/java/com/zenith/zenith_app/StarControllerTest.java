package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StarControllerTest extends ConfigurationSetupTest {

  protected Long id;
  protected String token;
  protected String constellation;
  protected Long constellationId;
  protected String star;
  protected Long starId;
  protected String starName;
  protected String starStatus;

  @BeforeAll
  void before() throws Exception {
    id = registerUser("register.json");
    token = extractTokenFromLogin("login_happyPath.json");

    // Creating a constellation and star for testing
    constellation = createConstellation();
    constellationId = objectMapper.readTree(constellation).get("id").asLong();
    star = createStar();
    starId = objectMapper.readTree(star).get("id").asLong();
    starName = objectMapper.readTree(star).get("name").asText();
    starStatus = objectMapper.readTree(star).get("status").asText();
  }

  @Test
  void testCreateStar_HappyPath() throws Exception {
    String starHappyPath = loadJson("star", "star_happyPath.json");
    ResultActions createStarRequest =
        mockMvc.perform(
            post("/api/star/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(starHappyPath));

    // Assert HTTP 200
    createStarRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Buy cat food for Sailor"))
        .andExpect(jsonPath("$.constellationId").value(constellationId))
        .andExpect(jsonPath("$.status").value("NOT_STARTED"));
  }

  @Test
  void testCreateStar_SadPath() throws Exception {
    String starSadPath = loadJson("star", "star_sadPath.json");
    ResultActions createStarRequest =
        mockMvc.perform(
            post("/api/star/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(starSadPath));

    // Assert HTTP 404
    createStarRequest.andExpect(status().isNotFound());
  }

  @Test
  void testViewStarById_HappyPath() throws Exception {
    ResultActions viewStarByIdRequest =
        mockMvc.perform(
            get("/api/star/view/id/" + starId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    viewStarByIdRequest.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
  }

  // Invalid StarId
  @Test
  void testViewStarById_SadPath() throws Exception {
    ResultActions viewStarByIdRequest =
        mockMvc.perform(
            get("/api/star/view/id/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 404
    viewStarByIdRequest.andExpect(status().isNotFound());
  }

  @Test
  void testViewStarByName() throws Exception {
    ResultActions viewStarByNameRequest =
        mockMvc.perform(
            get("/api/star/view/name")
                .param("starName", starName)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    viewStarByNameRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].name").value("Buy cat toys for Leïla and Sailor"));
  }

  @Test
  void testViewStarByStatus() throws Exception {
    ResultActions viewStarByStatusRequest =
        mockMvc.perform(
            get("/api/star/view/status/" + starStatus)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    viewStarByStatusRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));
  }

  @Test
  void testViewStarByConstellation_HappyPath() throws Exception {
    ResultActions viewStarByConstellationRequest =
        mockMvc.perform(
            get("/api/star/view/constellation/" + constellationId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    viewStarByConstellationRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].constellationId").value(constellationId));
  }

  @Test
  void testViewStarByConstellation_SadPath() throws Exception {
    ResultActions viewStarByConstellationRequest =
        mockMvc.perform(
            get("/api/star/view/constellation/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 404
    viewStarByConstellationRequest.andExpect(status().isNotFound());
  }

  @Test
  void testUpdateStar_HappyPath() throws Exception {
    ResultActions updateStarRequest =
        mockMvc.perform(
            put("/api/star/update/" + starId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"status\" : \"COMPLETED\"}")));

    // Assert HTTP 200
    updateStarRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("COMPLETED"));
  }

  // Invalid StarId
  @Test
  void testUpdateStar_SadPath() throws Exception {
    ResultActions updateStarRequest =
        mockMvc.perform(
            put("/api/star/update/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"status\" : \"COMPLETED\"}")));

    // Assert HTTP 404
    updateStarRequest.andExpect(status().isNotFound());
  }

  @Test
  void testDeleteStarById_SadPath() throws Exception {
    mockMvc
        .perform(delete("/api/star/delete/id/" + -1).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteStarByName_SadPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/star/delete/name/" + "test").header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @AfterAll
  void testDeleteStarById_HappyPath() throws Exception {
    ResultActions deleteStarRequest =
        mockMvc.perform(
            delete("/api/star/delete/id/" + starId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    deleteStarRequest.andExpect(status().isOk());
  }
}
