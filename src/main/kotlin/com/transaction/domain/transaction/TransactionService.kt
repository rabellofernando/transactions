package com.transaction.domain.transaction

import com.transaction.api.TransactionResponse
import com.transaction.api.TransactionVO
import com.transaction.domain.account.AccountService
import com.transaction.domain.transaction.enum.OperationType
import com.transaction.persistence.TransactionEntity
import com.transaction.persistence.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StopWatch
import java.util.UUID

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository, private val accountService: AccountService
) {

    private val logger = LoggerFactory.getLogger(TransactionService::class.java)

    @Transactional
    fun createTransaction(request: TransactionVO): TransactionResponse {
        val stopWatch = StopWatch()
        stopWatch.start()
        try {
            logger.info("m=createTransaction msg=start request=${request}")

            val account = accountService.findAccountEntity(request.codAccount)

            // maybe a strategy here l8r -- lets keep it simple for now =D
            val operationType = OperationType.fromId(request.operationTypeId)
            val finalAmount = if (operationType.isNegativeAmount()) {
                request.amount.negate()
            } else {
                request.amount
            }

            val transactionEntity = TransactionEntity(
                account = account,
                operationType = operationType,
                amount = finalAmount,
                codTransaction = UUID.randomUUID().toString()
            )

            val savedTransaction = transactionRepository.save(transactionEntity)

            return TransactionResponse(
                codTransaction = savedTransaction.codTransaction,
                codAccount = savedTransaction.account.codAccount,
                operationTypeId = savedTransaction.operationType.id,
                amount = savedTransaction.amount,
                eventDate = savedTransaction.datEvent
            )
        } finally {
            stopWatch.stop()
            logger.info("m=createTransaction msg=end elapsedTime=${stopWatch.totalTimeMillis}")
        }

    }


}
