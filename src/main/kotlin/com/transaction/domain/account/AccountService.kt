package com.transaction.domain.account

import com.transaction.api.AccountResponse
import com.transaction.persistence.AccountEntity
import com.transaction.persistence.AccountRepository
import com.transaction.domain.exception.AccountNotFoundException
import com.transaction.domain.exception.DuplicateDocumentNumberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository
) {

    @Transactional
    fun createAccount(request: AccountVO): AccountResponseVO {
        val documentNumber = request.documentNumber
        if (accountRepository.existsByDocumentNumber(documentNumber)) {
            throw DuplicateDocumentNumberException(documentNumber)
        }

        val account = AccountEntity(
            codAccount = UUID.randomUUID().toString(),
            documentNumber = documentNumber
        )

        val savedAccount = accountRepository.save(account)

        return AccountResponseVO(
            codAccount = savedAccount.codAccount,
            documentNumber = savedAccount.documentNumber
        )
    }

    @Transactional(readOnly = true)
    fun getAccount(codAccount: String): AccountResponseVO {
        val account = accountRepository.findByCodAccount(codAccount)
            .orElseThrow { AccountNotFoundException(codAccount) }

        return AccountResponseVO(
            codAccount = account.codAccount,
            documentNumber = account.documentNumber
        )
    }

    @Transactional(readOnly = true)
    fun findAccountEntity(codAccount: String): AccountEntity {
        return accountRepository.findByCodAccount(codAccount)
            .orElseThrow { AccountNotFoundException(codAccount) }
    }
}

data class AccountVO(val documentNumber: String)
data class AccountResponseVO(val codAccount: String,
                             val documentNumber: String)