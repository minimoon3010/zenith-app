package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConstellationControllerTest extends ConfigurationSetupTest {

  protected Long id;
  protected String token;
  protected String constellation;
  protected Long constellationId;
  protected String constellationName;

  @BeforeAll
  void before() throws Exception {
    id = registerUser("register.json");
    token = extractTokenFromLogin("login_happyPath.json");

    constellation = createConstellation();
    constellationId = objectMapper.readTree(constellation).get("id").asLong();
    constellationName = objectMapper.readTree(constellation).get("missionName").asText();
  }

  @Test
  void testCreateConstellation_HappyPath() throws Exception {
    String happyPathData = loadJson("constellation", "constellation_happyPath.json");
    ResultActions createConstellationRequest =
        mockMvc.perform(
            post("/api/constellation/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(happyPathData));

    // Assert HTTP 200
    createConstellationRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.missionName").value("Take Care of Sailor"))
        .andExpect(jsonPath("$.id").exists());
  }

  @Test
  void testCreateConstellation_SadPath() throws Exception {
    String sadPathData = loadJson("constellation", "constellation_sadPath.json");
    ResultActions createConstellationRequest =
        mockMvc.perform(
            post("/api/constellation/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(sadPathData));

    // Assert HTTP 400
    createConstellationRequest.andExpect(status().isBadRequest());
  }

  @Test
  void testViewConstellationById_HappyPath() throws Exception {
    ResultActions viewConstellationByIdRequest =
        mockMvc.perform(
            get("/api/constellation/view/id/" + constellationId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(constellation));

    // Assert HTTP 200
    viewConstellationByIdRequest.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
  }

  @Test
  void testViewConstellationByName_HappyPath() throws Exception {
    ResultActions viewConstellationByNameRequest =
        mockMvc.perform(
            get("/api/constellation/view/name/" + constellationName)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(constellation));

    // Assert HTTP 200
    viewConstellationByNameRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].missionName").exists())
        .andExpect(jsonPath("$[0].missionName").value("Take Care of Sailor and Leïla"));
  }

  @Test
  void testViewAllConstellations_HappyPath() throws Exception {
    ResultActions viewAllConstellationRequest =
        mockMvc.perform(
            get("/api/constellation/view/all")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(constellation));

    // Assert HTTP 200
    viewAllConstellationRequest.andExpect(status().isOk());
  }

  @Test
  void testUpdateConstellations_HappyPath() throws Exception {
    ResultActions updateConstellationRequest =
        mockMvc.perform(
            put("/api/constellation/update/" + constellationId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"objective\" : \"Take Care of Leïla first and then Sailor.\"}")));

    // Assert HTTP 200
    updateConstellationRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.missionName").exists())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.objective").value("Take Care of Leïla first and then Sailor."));
  }

  // Invalid ConstellationId
  @Test
  void testViewConstellationById_SadPath() throws Exception {
    mockMvc
        .perform(get("/api/constellation/view/id/" + -1).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @Test
  void testViewConstellationByName_SadPath() throws Exception {
    mockMvc
        .perform(
            get("/api/constellation/view/name/nonexistent")
                .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  // Invalid ConstellationId
  @Test
  void testUpdateConstellation_SadPath() throws Exception {
    mockMvc
        .perform(
            put("/api/constellation/update/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"objective\": \"test\"}"))
        .andExpect(status().isNotFound());
  }

  // Invalid ConstellationId
  @Test
  void testDeleteConstellation_SadPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/constellation/delete/id/" + -1).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }

  @AfterAll
  void testDeleteConstellationById_HappyPath() throws Exception {
    ResultActions deleteConstellationRequest =
        mockMvc.perform(
            delete("/api/constellation/delete/id/" + constellationId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON));

    // Assert HTTP 200
    deleteConstellationRequest.andExpect(status().isOk());
  }
}
