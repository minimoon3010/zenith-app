package com.zenith.zenith_app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MoodControllerTest extends ConfigurationSetupTest {

  protected Long id;
  protected String token;
  protected List<String> moods;
  protected Integer moodStatus4;
  protected Long moodId1;
  protected Long moodId2;
  protected Long moodId3;
  protected Long moodId4;

  @BeforeAll
  void beforeAll() throws Exception {
    id = registerUser();
    token = extractTokenFromLogin();
    moods = createMood();
    moodStatus4 = objectMapper.readTree(moods.get(3)).get("status").asInt();
    moodId1 = objectMapper.readTree(moods.get(0)).get("id").asLong();
    moodId2 = objectMapper.readTree(moods.get(1)).get("id").asLong();
    moodId3 = objectMapper.readTree(moods.get(2)).get("id").asLong();
    moodId4 = objectMapper.readTree(moods.get(3)).get("id").asLong();
  }

  @Test
  void testCreateMood_HappyPath() throws Exception {
    String moodHappy = loadJson("mood", "mood_happyPath.json");

    String response =
        mockMvc
            .perform(
                post("/api/mood/new")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(moodHappy))

            // Assert HTTP 200
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value(7))
            .andExpect(jsonPath("$.energy").value("Feeling productive today"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.lastUpdated").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode responseJson = objectMapper.readTree(response);
    LocalDateTime createdTimestamp = LocalDateTime.parse(responseJson.get("createdAt").asText());
    LocalDateTime lastUpdatedTimestamp =
        LocalDateTime.parse(responseJson.get("lastUpdated").asText());
    assertEquals(
        lastUpdatedTimestamp,
        createdTimestamp,
        "Last updated should have the same time upon creation.");
  }

  // Mood created with no status
  @Test
  void testCreateMood_SadPath() throws Exception {
    String moodSad = loadJson("mood", "mood_sadPath.json");
    mockMvc
        .perform(
            post("/api/mood/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(moodSad))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @Test
  void testUpdateMood_HappyPath() throws Exception {
    String updatedMood =
        mockMvc
            .perform(
                put("/api/mood/update/" + moodId4)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"energy\": \"Decent mood\"}")))

            // Assert HTTP 200
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value(moodStatus4))
            .andExpect(jsonPath("$.energy").value("Decent mood"))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.lastUpdated").exists())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode responseJson = objectMapper.readTree(updatedMood);
    LocalDateTime createdTimestamp = LocalDateTime.parse(responseJson.get("createdAt").asText());
    LocalDateTime updatedTimestamp = LocalDateTime.parse(responseJson.get("lastUpdated").asText());

    assertTrue(
        updatedTimestamp.isAfter(createdTimestamp),
        "Updated timestamp should be after created timestamp.");
  }

  // Invalid moodId
  @Test
  void testUpdateMood_SadPath() throws Exception {
    mockMvc
        .perform(
            put("/api/mood/update/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"energy\": \"Decent mood\"}")))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testViewAllMoods() throws Exception {
    String response =
        mockMvc
            .perform(
                get("/api/mood/view/all")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))

            // Assert HTTP 200
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode responseJson = objectMapper.readTree(response);
    List<Long> returnedIds = new ArrayList<>();
    List<Integer> returnedStatuses = new ArrayList<>();

    for (JsonNode moodNode : responseJson) {
      returnedIds.add(moodNode.get("id").asLong());
      returnedStatuses.add(moodNode.get("status").asInt());
    }

    for (String mood : moods) {
      JsonNode moodJson = objectMapper.readTree(mood);
      long expectedId = moodJson.get("id").asLong();
      int expectedStatus = moodJson.get("status").asInt();

      assertTrue(
          returnedIds.contains(expectedId), "Expected mood id " + expectedId + " in response.");
      assertTrue(
          returnedStatuses.contains(expectedStatus),
          "Expected status " + expectedStatus + " in response.");
    }
  }

  @Test
  void testViewMoodById() throws Exception {
    mockMvc
        .perform(
            get("/api/mood/view/id/" + moodId3)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == " + moodId3 + ")]").exists());
  }

  @Test
  void testFilterMoodByStatus() throws Exception {
    String response =
        mockMvc
            .perform(
                get("/api/mood/filter/status?status=" + 8)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON))

            // Assert HTTP 200
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode responseJson = objectMapper.readTree(response);

    // Every returned mood should actually have status 8 (filter correctness)
    for (JsonNode moodNode : responseJson) {
      assertEquals(8, moodNode.get("status").asInt());
    }

    // Both Mood 1 and Mood 2 should be present (completeness)
    List<Long> returnedIds = new ArrayList<>();
    for (JsonNode moodNode : responseJson) {
      returnedIds.add(moodNode.get("id").asLong());
    }
    assertTrue(returnedIds.contains(moodId1), "Expected moodId1 in filtered results.");
    assertTrue(returnedIds.contains(moodId2), "Expected moodId2 in filtered results.");
    assertEquals(2, responseJson.size());
  }

  @Test
  void testFilterMoodByTime_HappyPath() throws Exception {
    LocalDateTime earlier = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    LocalDateTime later = LocalDateTime.of(2026, 12, 31, 0, 0, 0);

    mockMvc
        .perform(
            get("/api/mood/filter/time")
                .param("earlier", earlier.toString())
                .param("later", later.toString())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isNotEmpty());
  }

  @Test
  void testFilterMoodByTime_SadPath() throws Exception {
    LocalDateTime earlier = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
    LocalDateTime later = LocalDateTime.of(2023, 12, 31, 0, 0, 0);

    mockMvc
        .perform(
            get("/api/mood/filter/time")
                .param("earlier", earlier.toString())
                .param("later", later.toString())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200 with empty List
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
