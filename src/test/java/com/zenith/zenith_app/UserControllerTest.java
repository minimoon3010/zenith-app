package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class UserControllerTest extends ConfigurationSetupTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  protected String token;

  protected Long id;

  @BeforeAll
  void before() throws Exception {
    id = registerUser();
    token = extractTokenFromLogin();
  }

  @Test
  void testViewProfile_HappyPath() throws Exception {
    mockMvc
        .perform(get("/api/users/" + id).header("Authorization", "Bearer " + token))

        // Verify that HTTP 200 is returned
        .andExpect(status().isOk());
  }

  @Test
  void testViewProfile_SadPath() throws Exception {
    mockMvc
        .perform(get("/api/users/0").header("Authorization", "Bearer " + token))

        // Verify that HTTP 401 is returned -> users should not be able to view others' profiles
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testUpdateProfile_HappyPath() throws Exception {
    String updateProfileData = loadJson("updateProfile.json");

    List<Object> updateOptions =
        objectMapper.readValue(updateProfileData, new TypeReference<>() {});

    for (Object update : updateOptions) {

      mockMvc
          .perform(
              put("/api/users/" + id)
                  .header("Authorization", "Bearer " + token)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(update)))

          // Verify that HTTP 200 is returned
          .andExpect(status().isOk());
    }
  }

  @Test
  void testUpdateProfile_SadPath_incorrectUser() throws Exception {
    mockMvc
        .perform(
            put("/api/users/0")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"firstName\" : \"Candy\"}")))

        // Verify that HTTP 401 is returned
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testUpdateProfile_SadPath_noToken() throws Exception {
    mockMvc
        .perform(
            put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"firstName\" : \"Candy\"}")))

        // Verify that HTTP 401 is returned - without a token they cannot be verified.
        .andExpect(status().isUnauthorized());
  }
}
