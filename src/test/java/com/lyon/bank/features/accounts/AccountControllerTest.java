package com.lyon.bank.features.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyon.bank.features.accounts.dtos.AccountDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AccountService accountService;

  @Test
  @DisplayName("Should return 201 ok when an account is created")
  @WithMockUser(username = "testUser")
  void shouldReturnWhenUserIsCreated() throws Exception {
    // 1. ARRANGE
    AccountDTO expectedAccount = new AccountDTO(
      1L, "accountNumber", "cci", BigDecimal.TEN
    );

    Mockito.when(accountService.createAccount()).thenReturn(expectedAccount);

    // 2. ACT & 3. ASSERT
    mockMvc.perform(post("/api/accounts")
          .with(csrf())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString())
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.accountNumber").value("accountNumber"))
      .andExpect(jsonPath("$.balance").value(BigDecimal.TEN))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("Should return 200 ok and list of accounts")
  @WithMockUser(username = "testUser")
  void shouldReturnOkWhenGetAllAccounts() throws Exception {
    // 1. ARRANGE
    List<AccountDTO> accountDTOS = new ArrayList<>();
    AccountDTO accountDTO1 = new AccountDTO(1L, "accNumber1", "cci1", BigDecimal.TEN);
    AccountDTO accountDTO2 = new AccountDTO(2L, "accNumber2", "cci2", BigDecimal.TWO);
    accountDTOS.add(accountDTO1);
    accountDTOS.add(accountDTO2);

    Mockito.when(accountService.getMyAccounts()).thenReturn(accountDTOS);

    // 2. ACT & ASSERT
    mockMvc.perform(get("/api/accounts")
//                    .with(csrf())
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].id").value(1L))
      .andExpect(jsonPath("$[1].balance").value(BigDecimal.TWO));
  }

  @Test
  @DisplayName("Should throw an error when user not found")
  @WithMockUser(username = "userTest")
  void shouldThrowAnErrorWhenUserNotFound() throws Exception {
    // 1. ARRANGE
    Mockito.when(accountService.getMyAccounts())
      .thenThrow(new RuntimeException("User not found"));

    // 2. ACT & ASSERT
//    mockMvc.perform(get("/api/accounts"))
//      .andExpect(status().isInternalServerError());
    Assertions.assertThrows(jakarta.servlet.ServletException.class, () -> {
      mockMvc.perform(get("/api/accounts"));
    });
  }
}
