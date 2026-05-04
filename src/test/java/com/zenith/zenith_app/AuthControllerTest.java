package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  void before() throws Exception {
    String registerData = loadJson("register.json");

    mockMvc.perform(
        post("/api/users/register").contentType(MediaType.APPLICATION_JSON).content(registerData));
  }

  @Test
  void testLogin_HappyPath() throws Exception {
    String happyPathData = loadJson("login_happyPath.json");

    List<Object> happyPaths = objectMapper.readValue(happyPathData, new TypeReference<>() {});

    for (Object happyPath : happyPaths) {

      ResultActions loginRequest =
          mockMvc.perform(
              post("/api/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(happyPath)));

      // Assert HTTP 200
      loginRequest.andExpect(status().isOk()).andExpect(jsonPath("$.token").exists());
    }
  }

  @Test
  void testLogin_SadPath() throws Exception {
    String sadPathData = loadJson("login_sadPath.json");

    List<Object> sadPaths = objectMapper.readValue(sadPathData, new TypeReference<>() {});

    for (Object sadPath : sadPaths) {

      ResultActions loginRequest =
          mockMvc.perform(
              post("/api/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(sadPath)));

      // Assert HTTP 401
      loginRequest.andExpect(status().isUnauthorized());
    }
  }

  private String loadJson(String filename) throws Exception {
    return new String(
        Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("data/" + filename))
            .readAllBytes());
  }
}
