package com.zenith.zenith_app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  protected Long registerUser() throws Exception {
    String registerData = loadJson("register.json");

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

  protected String extractTokenFromLogin() throws Exception {
    String loginData =
        new String(
            Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream("data/login_happyPath.json"))
                .readAllBytes());

    List<Object> logins = objectMapper.readValue(loginData, new TypeReference<>() {});
    String singleLogin = objectMapper.writeValueAsString(logins.get(0));

    String response =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(singleLogin))
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readTree(response).get("token").asText();
  }

  protected String loadJson(String filename) throws Exception {
    return new String(
        Objects.requireNonNull(
                ConfigurationSetupTest.class
                    .getClassLoader()
                    .getResourceAsStream("data/" + filename))
            .readAllBytes());
  }
}
