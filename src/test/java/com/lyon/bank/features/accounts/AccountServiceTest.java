package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import com.lyon.bank.features.security.RoleEntity;
import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import com.lyon.bank.shared.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
//@RequiredArgsConstructor
public class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @InjectMocks
  private AccountService accountService;

  @BeforeEach
  void setup() {
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should create account successfully when user is authenticated")
  void shouldCreateAccountSuccessfully() {
    //1.ARRANGE
    String usernameTest = "usernameTest";
    UserEntity mockUser = UserEntity.builder()
      .id(1L).username(usernameTest).build();

    // simulate the behavior of the security context
    Mockito.when(securityContext.getAuthentication())
      .thenReturn(authentication);
    Mockito.when(authentication.getName())
        .thenReturn(usernameTest);

    // simulate the behavior if user exists in db
    Mockito.when(userRepository.findByUsername(usernameTest))
      .thenReturn(Optional.of(mockUser));

    // simulate the creation of a account
    Mockito.when(accountRepository.save(ArgumentMatchers.any(AccountEntity.class)))
      .thenAnswer( invocation -> {
        AccountEntity accountToSave = invocation.getArgument(0);
        accountToSave.setId(100L);
        return accountToSave;
      });

    //2.ACT
    AccountDTO result = accountService.createAccount();

    //3.ASSERT
    Assertions.assertNotNull(result);
    Assertions.assertEquals(BigDecimal.ZERO, result.balance());
    Assertions.assertTrue(
      result.cci().startsWith("002"),
      "CCI should start with bank code 002"
    );
    Assertions.assertEquals(
      14, result.accountNumber().length(),
      "Account number should be 14 chars"
    );

    Mockito.verify(accountRepository).save(ArgumentMatchers.any(AccountEntity.class));
  }

  @Test
  @DisplayName("Should throw exception when authenticated user is not found in DB")
  void shouldThrowExceptionWhenUserNotFound(){

    // 1.ARRANGE
    String usernameTest = "usernameTest";

    // simulate SecurityContext
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn(usernameTest);

    // simulate that user doesn't exists
    Mockito.when(userRepository.findByUsername(usernameTest))
      .thenReturn(Optional.empty());

    // 2. ACT
    Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
      accountService.createAccount();
    }) ;

    // 3. ASSERT
    Assertions.assertEquals("Authenticated user not found", exception.getMessage());

    // verifying that never try to save
    Mockito.verify(accountRepository, Mockito.never()).save(ArgumentMatchers.any());

  }

}
