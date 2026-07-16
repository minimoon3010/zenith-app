package com.zenith.zenith_app;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.zenith.zenith_app.transaction.TransactionCategory;
import com.zenith.zenith_app.transaction.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
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
public class TransactionControllerTest extends ConfigurationSetupTest {

  protected Long id;
  protected String token;
  protected List<String> transactions;
  protected long transactionId1;
  protected long transactionId2;
  protected long transactionId3;
  protected long transactionId4;
  protected String updatedTransaction2Name;
  protected String voidCategory = "VOID";

  @BeforeAll
  void before() throws Exception {
    id = registerUser("register.json");
    token = extractTokenFromLogin("login_happyPath.json");
    transactions = createTransaction();
    transactionId1 = objectMapper.readTree(transactions.get(0)).get("id").asLong();
    transactionId2 = objectMapper.readTree(transactions.get(1)).get("id").asLong();
    transactionId3 = objectMapper.readTree(transactions.get(2)).get("id").asLong();
    transactionId4 = objectMapper.readTree(transactions.get(3)).get("id").asLong();
  }

  @Test
  void testCreateTransaction_HappyPath() throws Exception {
    String transaction = loadJson("transaction", "transaction_happyPath.json");
    ResultActions createTransactionRequest =
        mockMvc.perform(
            post("/api/transaction/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transaction));

    // Assert HTTP 200
    createTransactionRequest
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.transactionName").value("Squeaky Toy for Sailor"))
        .andExpect(jsonPath("$.amount").value(11.00))
        .andExpect(jsonPath("$.transactionCreated").exists())
        .andExpect(jsonPath("$.lastUpdated").exists())
        .andExpect(jsonPath("$.transactionType").value("IMPULSE"))
        .andExpect(jsonPath("$.transactionCategory").value("OTHER"));
  }

  @Test
  void testCreateTransaction_SadPath() throws Exception {
    String transaction = loadJson("transaction", "transaction_sadPath.json");
    ResultActions createTransactionRequest =
        mockMvc.perform(
            post("/api/transaction/new")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(transaction));

    // Assert HTTP 400 because creating transaction with DISMISSED type is not allowed.
    createTransactionRequest.andExpect(status().isBadRequest());
  }

  @Test
  void viewTransactionById_HappyPath() throws Exception {
    for (String transaction : transactions) {
      mockMvc
          .perform(
              get("/api/transaction/view/id/"
                      + objectMapper.readTree(transaction).get("id").asLong())
                  .header("Authorization", "Bearer " + token)
                  .contentType(MediaType.APPLICATION_JSON))
          // Assert HTTP 200
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").exists());
    }
  }

  @Test
  void viewTransactionById_SadPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/view/id/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void viewAllTransactions() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/view/all")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
        // Assert HTTP 200
        .andExpect(status().isOk());
  }

  @Test
  void viewTransactionByName_HappyPath() throws Exception {
    for (String transaction : transactions) {
      mockMvc
          .perform(
              get("/api/transaction/view/name/"
                      + objectMapper.readTree(transaction).get("transactionName").asText())
                  .header("Authorization", "Bearer " + token)
                  .contentType(MediaType.APPLICATION_JSON))
          // Assert HTTP 200
          .andExpect(status().isOk())
          .andExpect(
              jsonPath(
                      "$[?(@.id == " + objectMapper.readTree(transaction).get("id").asLong() + ")]")
                  .exists())
          .andExpect(
              jsonPath(
                      "$[?(@.transactionName == '"
                          + objectMapper.readTree(transaction).get("transactionName").asText()
                          + "')]")
                  .exists());
    }
  }

  // transaction with this name does not exist
  @Test
  void viewTransactionByName_SadPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/view/name/" + "nonexistent transaction")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateTransaction_HappyPath() throws Exception {
    String transaction1 =
        mockMvc
            .perform(
                put("/api/transaction/update/" + transactionId1)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(("{\"transactionType\": \"DISMISSED\"}")))

            // Assert HTTP 200
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.transactionType").value("DISMISSED"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode updated1 = objectMapper.readTree(transaction1);
    LocalDateTime createdTimestamp1 =
        LocalDateTime.parse(updated1.get("transactionCreated").asText());
    LocalDateTime lastUpdatedTimestamp1 = LocalDateTime.parse(updated1.get("lastUpdated").asText());
    assertTrue(
        lastUpdatedTimestamp1.isAfter(createdTimestamp1),
        "lastUpdated should be after transactionCreated");

    String transaction2 =
        mockMvc
            .perform(
                put("/api/transaction/update/" + transactionId2)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"transactionCategory\": \"FOOD\", \"transactionName\": \"Buy cat food for Sailor\"}"))

            // Assert HTTP 200
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.transactionName").value("Buy cat food for Sailor"))
            .andExpect(jsonPath("$.transactionCategory").value("FOOD"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode updated2 = objectMapper.readTree(transaction2);
    updatedTransaction2Name = updated2.get("transactionName").asText();
    LocalDateTime createdTimestamp2 =
        LocalDateTime.parse(updated2.get("transactionCreated").asText());
    LocalDateTime lastUpdatedTimestamp2 = LocalDateTime.parse(updated2.get("lastUpdated").asText());
    assertTrue(
        lastUpdatedTimestamp2.isAfter(createdTimestamp2),
        "lastUpdated should be after transactionCreated");
  }

  // Invalid TransactionId
  @Test
  void testUpdateTransactionInvalidID_SadPath() throws Exception {
    mockMvc
        .perform(
            put("/api/transaction/update/" + -1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"transactionType\" : \"IMPULSE\"}")))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  // Trying to edit a completed transaction
  @Test
  void testUpdateTransactionCompletedTransaction_SadPath() throws Exception {
    mockMvc
        .perform(
            put("/api/transaction/update/" + transactionId3)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(("{\"transactionType\" : \"DISMISSED\"}")))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @Test
  void testFilterTransactionByCategory_SadPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/category/" + voidCategory)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @Test
  void testFilterTransactionByCategory_HappyPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/category/" + TransactionCategory.OTHER)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk());
  }

  @Test
  void testFilterTransactionByType_SadPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/type/" + voidCategory)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @Test
  void testFilterTransactionByType_HappyPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/type/" + TransactionType.POTENTIAL)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk());
  }

  @Test
  void testFilterTransactionByAmount_SadPath_NotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/amount")
                .param("lower", "1000")
                .param("higher", "10000")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testFilterTransactionByAmount_SadPath_InvalidInput() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/amount")
                .param("lower", "1000")
                .param("higher", "10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @Test
  void testFilterTransactionByAmount_HappyPath() throws Exception {
    mockMvc
        .perform(
            get("/api/transaction/filter/amount")
                .param("lower", "1")
                .param("higher", "100")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk());
  }

  @Test
  void testFilterTransactionByTimestamp_SadPath() throws Exception {
    LocalDateTime earlier = LocalDateTime.of(2023, 1, 1, 0, 0, 0);
    LocalDateTime later = LocalDateTime.of(2023, 12, 31, 0, 0, 0);

    mockMvc
        .perform(
            get("/api/transaction/filter/time")
                .param("earlier", earlier.toString())
                .param("later", later.toString())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testFilterTransactionByTimestamp_HappyPath() throws Exception {
    LocalDateTime earlier = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    LocalDateTime later = LocalDateTime.of(2026, 12, 31, 0, 0, 0);

    mockMvc
        .perform(
            get("/api/transaction/filter/time")
                .param("earlier", earlier.toString())
                .param("later", later.toString())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 200
        .andExpect(status().isOk());
  }

  @Test
  void testDeleteTransactionById_SadPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/transaction/delete/id/" + -1).header("Authorization", "Bearer " + token))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteTransactionByName_SadPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/transaction/delete/name/" + "test")
                .header("Authorization", "Bearer " + token))

        // Assert HTTP 404
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteTransactionThatIsImpulse_SadPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/transaction/delete/id/" + transactionId4)
                .header("Authorization", "Bearer " + token))

        // Assert HTTP 400
        .andExpect(status().isBadRequest());
  }

  @AfterAll
  void testDeleteTransactionById_HappyPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/transaction/delete/id/" + transactionId1)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 204
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            delete("/api/transaction/delete/id/" + transactionId3)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 204
        .andExpect(status().isNoContent());
  }

  @AfterAll
  void testDeleteTransactionByName_HappyPath() throws Exception {
    mockMvc
        .perform(
            delete("/api/transaction/delete/name/" + updatedTransaction2Name)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))

        // Assert HTTP 204
        .andExpect(status().isNoContent());
  }
}
