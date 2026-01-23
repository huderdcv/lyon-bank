package com.lyon.bank.features.accounts;

import com.lyon.bank.features.accounts.dtos.AccountDTO;
import com.lyon.bank.features.security.RoleEntity;
import com.lyon.bank.features.security.UserEntity;
import com.lyon.bank.features.security.UserRepository;
import com.lyon.bank.shared.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
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
import java.util.*;

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
      .thenAnswer(invocation -> {
        AccountEntity accountToSave = invocation.getArgument(0);
        if(accountToSave.getUser() == null) {
          throw new RuntimeException("User was not set on AccountEntity");
        }
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
  void shouldThrowExceptionWhenUserNotFound() {

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
    });

    // 3. ASSERT
    Assertions.assertEquals("Authenticated user not found", exception.getMessage());

    // verifying that never try to save
    Mockito.verify(accountRepository, Mockito.never()).save(ArgumentMatchers.any());
  }

  @Test
  @DisplayName("Receive all the accounts when authenticated user exists")
  void findAllAccountsWhenUserExists() {
    // 1.ARRANGE
    String username = "usernameTest";
    UserEntity mockUser = UserEntity.builder()
      .id(1L).username(username).build();
    AccountEntity mockAccount = AccountEntity.builder()
      .accountNumber("12345678901234")
      .balance(BigDecimal.TEN)
      .id(100L).build();
    List<AccountEntity> accounts = new ArrayList<>(List.of(mockAccount));

    // preparing mocks
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn(username);
    Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
    Mockito.when(accountRepository.findByUserId(mockUser.getId())).thenReturn(accounts);

    // 2. ACT
    List<AccountDTO> accountsInDb = accountService.getMyAccounts();

    // 3. ASSERT
    Assertions.assertNotNull(accountsInDb);
    Assertions.assertEquals(1, accountsInDb.size());

    AccountDTO accountDTO = accountsInDb.getFirst();
    Assertions.assertEquals(BigDecimal.TEN, accountDTO.balance());
    Assertions.assertEquals(mockAccount.getAccountNumber(), accountDTO.accountNumber());


    Mockito.verify(accountRepository).findByUserId(mockUser.getId());
  }

  @Test
  @DisplayName("Should return empty list when user exists but has no accounts")
  void shouldReturnEmptyListWhenUserHasNoAccounts() {
    // 1. ARRANGE
    String username = "usernameTest";
    Long userId = 1L;
    UserEntity userMock = UserEntity.builder()
      .id(userId)
      .username(username)
      .build();
    List<AccountEntity> accounts = new ArrayList<>();

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn(username);
    Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(userMock));
    Mockito.when(accountRepository.findByUserId(userId)).thenReturn(accounts);

    // 2. ACT
    List<AccountDTO> accountDTOSInDB = accountService.getMyAccounts();

    // 3. ASSERT
    Assertions.assertNotNull(accountDTOSInDB);
    Assertions.assertTrue(accountDTOSInDB.isEmpty());

    Mockito.verify(accountRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Should throw exception when authenticated user is not found in DB")
  void shouldThrowExceptionGetMyAccountsWhenUserNotFound() {
    // 1. ARRANGE
    String username = "usernameTest";

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    Mockito.when(authentication.getName()).thenReturn(username);
    Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    // 2. ACT
    Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
      accountService.getMyAccounts();
    });

    // 3. ASSERT
    Assertions.assertEquals("User not found", exception.getMessage());

    Mockito.verify(accountRepository, Mockito.never()).findByUserId(ArgumentMatchers.any());
  }


}
