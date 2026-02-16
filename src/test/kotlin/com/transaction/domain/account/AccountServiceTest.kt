package com.transaction.domain.account

import com.transaction.domain.exception.AccountNotFoundException
import com.transaction.domain.exception.DuplicateDocumentNumberException
import com.transaction.persistence.AccountEntity
import com.transaction.persistence.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("AccountService Unit Tests")
@ExtendWith(MockKExtension::class)
class AccountServiceTest {

    @MockK
    private lateinit var accountRepository: AccountRepository

    @InjectMockKs
    private lateinit var accountService: AccountService


    @Test
    @DisplayName("Should create account successfully")
    fun `should create account successfully`() {
        val request = AccountVO("12345678900")
        val accountId = UUID.randomUUID().toString();
        val account = getAccountEntity(accountId)

        every { accountRepository.existsByDocumentNumber("12345678900") } returns false
        every { accountRepository.save(any()) } returns account

        val result = accountService.createAccount(request)

        assertNotNull(result)
        assertEquals(accountId, result.codAccount)
        assertEquals("12345678900", result.documentNumber)

        verify(exactly = 1) { accountRepository.existsByDocumentNumber("12345678900") }
        verify(exactly = 1) { accountRepository.save(any()) }
    }


    fun getAccountEntity(accountId: String): AccountEntity {
        return AccountEntity(
            documentNumber = "12345678900",
            id = 1L,
            codAccount = accountId,
            datCreation = LocalDateTime.now()
        )
    }

    @Test
    @DisplayName("Should throw exception when creating account with duplicate document")
    fun `should throw exception when creating account with duplicate document`() {
        val request = AccountVO("12345678900")

        every { accountRepository.existsByDocumentNumber("12345678900") } returns true

        val exception = assertThrows<DuplicateDocumentNumberException> {
            accountService.createAccount(request)
        }

        assertEquals("Account with document number 12345678900 already exists", exception.message)
        verify(exactly = 1) { accountRepository.existsByDocumentNumber("12345678900") }
        verify(exactly = 0) { accountRepository.save(any()) }
    }

    @Test
    @DisplayName("Should get account by ID successfully")
    fun `should get account by ID successfully`() {
        val accountId = UUID.randomUUID().toString();
        val account = getAccountEntity(accountId)


        every { accountRepository.findByCodAccount(accountId) } returns Optional.of(account)

        val result = accountService.getAccount(accountId)

        assertNotNull(result)
        assertEquals(accountId, result.codAccount)
        assertEquals("12345678900", result.documentNumber)

        verify(exactly = 1) { accountRepository.findByCodAccount(accountId) }
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent account")
    fun `should throw exception when getting non-existent account`() {
        val codAccount = UUID.randomUUID().toString();

        every { accountRepository.findByCodAccount(codAccount) } returns Optional.empty()

        val exception = assertThrows<AccountNotFoundException> {
            accountService.getAccount(codAccount)
        }

        assertEquals("Account with ID $codAccount not found", exception.message)
        verify(exactly = 1) { accountRepository.findByCodAccount(codAccount) }
    }


    @Test
    @DisplayName("Should find account entity successfully")
    fun `should find account entity successfully`() {
        val codAccount = UUID.randomUUID().toString();
        val account = getAccountEntity(codAccount)

        every { accountRepository.findByCodAccount(codAccount) } returns Optional.of(account)

        val result = accountService.findAccountEntity(codAccount)

        assertNotNull(result)
        assertEquals(codAccount, result.codAccount)
        assertEquals("12345678900", result.documentNumber)

    }

    @Test
    @DisplayName("Should throw exception when finding non-existent account entity")
    fun `should throw exception when finding non-existent account entity`() {
        val codAccount = UUID.randomUUID().toString();

        every { accountRepository.findByCodAccount(codAccount) } returns Optional.empty()

        assertThrows<AccountNotFoundException> {
            accountService.findAccountEntity(codAccount)
        }

    }

}