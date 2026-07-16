package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ConfigurationSetupTest {

  @Autowired protected MockMvc mockMvc;

  protected final ObjectMapper objectMapper = new ObjectMapper();
  protected String response;

  protected Long registerUser(String filename) throws Exception {
    String registerData = loadJson("user", filename);

    String response =
        mockMvc
            .perform(
                post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(registerData))
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response).get("id").asLong();
  }

  protected String extractTokenFromLogin(String filename) throws Exception {
    String loginData = loadJson("user", filename);

    if (filename.equals("loginOtherUser.json")) {
      response =
          mockMvc
              .perform(
                  post("/api/auth/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(loginData))
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();
    } else {
      List<Object> logins = objectMapper.readValue(loginData, new TypeReference<>() {});
      String singleLogin = objectMapper.writeValueAsString(logins.get(0));
      response =
          mockMvc
              .perform(
                  post("/api/auth/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(singleLogin))
              .andDo(print())
              .andReturn()
              .getResponse()
              .getContentAsString();
    }

    return objectMapper.readTree(response).get("token").asText();
  }

  protected String loadJson(String folder, String filename) throws Exception {
    return new String(
        Objects.requireNonNull(
                ConfigurationSetupTest.class
                    .getClassLoader()
                    .getResourceAsStream("data/" + folder + "/" + filename))
            .readAllBytes());
  }

  protected String createConstellation() throws Exception {
    String constellation = loadJson("constellation", "constellation.json");
    return mockMvc
        .perform(
            post("/api/constellation/new")
                .header("Authorization", "Bearer " + extractTokenFromLogin("login_happyPath.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(constellation))
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  protected String createStar() throws Exception {
    String star = loadJson("star", "star.json");
    return mockMvc
        .perform(
            post("/api/star/new")
                .header("Authorization", "Bearer " + extractTokenFromLogin("login_happyPath.json"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(star))
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  protected List<String> createTransaction() throws Exception {
    String transactionData = loadJson("transaction", "transaction.json");
    List<Object> transactions = objectMapper.readValue(transactionData, new TypeReference<>() {});
    List<String> responses = new ArrayList<>();

    for (Object transaction : transactions) {
      String response =
          mockMvc
              .perform(
                  post("/api/transaction/new")
                      .header(
                          "Authorization",
                          "Bearer " + extractTokenFromLogin("login_happyPath.json"))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(transaction)))
              .andReturn()
              .getResponse()
              .getContentAsString();

      responses.add(response);
    }
    return responses;
  }

  protected List<String> createMood() throws Exception {
    String moodData = loadJson("mood", "mood.json");
    List<Object> moods = objectMapper.readValue(moodData, new TypeReference<>() {});
    List<String> responses = new ArrayList<>();
    for (Object mood : moods) {
      String response =
          mockMvc
              .perform(
                  post("/api/mood/new")
                      .header(
                          "Authorization",
                          "Bearer " + extractTokenFromLogin("login_happyPath.json"))
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(mood)))
              .andReturn()
              .getResponse()
              .getContentAsString();
      responses.add(response);
    }
    return responses;
  }
}
