package com.transaction.domain.transaction

import com.transaction.api.TransactionVO
import com.transaction.domain.account.AccountService
import com.transaction.domain.exception.AccountNotFoundException
import com.transaction.domain.exception.OperationTypeNotFoundException
import com.transaction.domain.transaction.enum.OperationType
import com.transaction.persistence.AccountEntity
import com.transaction.persistence.TransactionRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("TransactionService Unit Tests")
@ExtendWith(MockKExtension::class)
class TransactionServiceTest {

    @MockK
    private lateinit var transactionRepository: TransactionRepository

    @MockK
    private lateinit var accountService: AccountService

    @InjectMockKs
    private lateinit var transactionService: TransactionService


    @Test
    @DisplayName("Should create transaction with negative amount for purchase")
    fun `should create transaction with negative amount for purchase`() {
        val codAccount = UUID.randomUUID().toString()
        val account = getAccountEntity(codAccount)
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = OperationType.PURCHASE.id,
            amount = BigDecimal("50.00")
        )

        every { accountService.findAccountEntity(codAccount) } returns account
        every { transactionRepository.save(any()) } returnsArgument 0

        val result = transactionService.createTransaction(request)

        assertNotNull(result)
        assertEquals(codAccount, result.codAccount)
        assertEquals(1L, result.operationTypeId)
        assertEquals(BigDecimal("-50.00"), result.amount)
        assertNotNull(result.eventDate)
        assertNotNull(result.codTransaction)

        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 1) { transactionRepository.save(any()) }
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
    @DisplayName("Should create transaction with positive amount for credit")
    fun `should create transaction with positive amount for credit`() {
        val codAccount = UUID.randomUUID().toString()
        val account = getAccountEntity(codAccount)
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = OperationType.CREDIT_VOUCHER.id,
            amount = BigDecimal("50.00")
        )

        every { accountService.findAccountEntity(codAccount) } returns account
        every { transactionRepository.save(any()) } returnsArgument 0

        val result = transactionService.createTransaction(request)

        assertNotNull(result)
        assertEquals(codAccount, result.codAccount)
        assertEquals(4L, result.operationTypeId)
        assertEquals(BigDecimal("50.00"), result.amount)
        assertNotNull(result.eventDate)
        assertNotNull(result.codTransaction)

        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 1) { transactionRepository.save(any()) }

    }

    @Test
    @DisplayName("Should create purchase with installments as negative amount")
    fun `should create purchase with installments as negative amount`() {
        val codAccount = UUID.randomUUID().toString()
        val account = getAccountEntity(codAccount)
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = OperationType.PURCHASE_INSTALLMENTS.id,
            amount = BigDecimal("233.34")
        )

        every { accountService.findAccountEntity(codAccount) } returns account
        every { transactionRepository.save(any()) } returnsArgument 0

        val result = transactionService.createTransaction(request)

        assertNotNull(result)
        assertEquals(codAccount, result.codAccount)
        assertEquals(2L, result.operationTypeId)
        assertEquals(BigDecimal("-233.34"), result.amount)
        assertNotNull(result.eventDate)
        assertNotNull(result.codTransaction)

        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 1) { transactionRepository.save(any()) }
    }

    @Test
    @DisplayName("Should create withdrawal as negative amount")
    fun `should create withdrawal as negative amount`() {
        val codAccount = UUID.randomUUID().toString()
        val account = getAccountEntity(codAccount)
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = OperationType.WITHDRAWAL.id,
            amount = BigDecimal("233.34")
        )

        every { accountService.findAccountEntity(codAccount) } returns account
        every { transactionRepository.save(any()) } returnsArgument 0

        val result = transactionService.createTransaction(request)

        assertNotNull(result)
        assertEquals(codAccount, result.codAccount)
        assertEquals(3L, result.operationTypeId)
        assertEquals(BigDecimal("-233.34"), result.amount)
        assertNotNull(result.eventDate)
        assertNotNull(result.codTransaction)

        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 1) { transactionRepository.save(any()) }
    }

    @Test
    @DisplayName("Should throw exception when creating transaction for non-existent account")
    fun `should throw exception when creating transaction for non-existent account`() {
        val codAccount = UUID.randomUUID().toString()
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = OperationType.WITHDRAWAL.id,
            amount = BigDecimal("233.34")
        )

        every { accountService.findAccountEntity(codAccount) } throws AccountNotFoundException(codAccount)

        val assertThrows = assertThrows<AccountNotFoundException> {
            transactionService.createTransaction(request)
        }


        assertEquals("Account with ID $codAccount not found", assertThrows.message)
        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 0) { transactionRepository.save(any()) }
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with invalid operation type")
    fun `should throw exception when creating transaction with invalid operation type`() {
        val codAccount = UUID.randomUUID().toString()
        val account = getAccountEntity(codAccount)
        val request = TransactionVO(
            codAccount = codAccount,
            operationTypeId = 999L,
            amount = BigDecimal("50.00")
        )


        every { accountService.findAccountEntity(codAccount) } returns account

        assertThrows<OperationTypeNotFoundException> {
            transactionService.createTransaction(request)
        }

        verify(exactly = 1) { accountService.findAccountEntity(codAccount) }
        verify(exactly = 0) { transactionRepository.save(any()) }
    }
}