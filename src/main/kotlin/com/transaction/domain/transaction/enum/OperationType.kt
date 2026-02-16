package com.transaction.domain.transaction.enum

import com.transaction.domain.exception.OperationTypeNotFoundException

enum class OperationType(val id: Long) {
    PURCHASE(1),
    PURCHASE_INSTALLMENTS(2),
    WITHDRAWAL(3),
    CREDIT_VOUCHER(4);

    fun isNegativeAmount(): Boolean {
        return this in debitOperation()
    }


    companion object {
        fun fromId(operationId: Long): OperationType {
            val result = entries.toTypedArray().find { it -> it.id == operationId }
            return result ?: throw OperationTypeNotFoundException(operationId)
        }
        fun creditOperations(): Set<OperationType> {
            return setOf(CREDIT_VOUCHER)
        }
        fun debitOperation(): Set<OperationType> {
            return setOf(PURCHASE, PURCHASE_INSTALLMENTS, WITHDRAWAL)
        }
    }
}