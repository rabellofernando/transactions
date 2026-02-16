package com.transaction.domain.exception

open class NotFoundException(message: String) : RuntimeException(message)

class AccountNotFoundException(accountId: Any) : NotFoundException("Account with ID $accountId not found")
class OperationTypeNotFoundException(operationTypeId: Long) : NotFoundException("Operation type with ID $operationTypeId not found")
class DuplicateDocumentNumberException(documentNumber: String) :
    RuntimeException("Account with document number $documentNumber already exists")
class InvalidAmountException(message: String) :
    RuntimeException(message)
