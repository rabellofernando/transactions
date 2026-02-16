package com.transaction.api

import com.transaction.domain.transaction.TransactionService
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.LocalDateTime
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

@RestController
@RequestMapping("/transaction")
class TransactionEndpoint(
    private val transactionService: TransactionService
) {

    @PostMapping
    @Operation(
        summary = "Create new transaction",
        description = """
            Records a new transaction for an account.
            
            **Operation Types:**
            - 1: Normal Purchase
            - 2: Purchase with installments
            - 3: Withdrawal
            - 4: Credit Voucher
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Transaction created successfully",
                content = [Content(schema = Schema(implementation = TransactionResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid data (amount must be positive)",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Account or operation type not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun createTransaction(
        @Valid @RequestBody request: TransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val transactionVO = TransactionVO(
            codAccount = request.codAccount!!,
            amount = request.amount!!,
            operationTypeId = request.operationTypeId!!
        )
        val response = transactionService.createTransaction(transactionVO)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}

data class TransactionRequest(
    @field:NotNull(message = "Account ID is required")
    @JsonProperty("account_id")
    val codAccount: String?,

    @field:NotNull(message = "Operation type ID is required")
    @JsonProperty("operation_type_id")
    @Schema(
        description = """
            Operation type:
            1 = Normal Purchase
            2 = Purchase with installments
            3 = Withdrawal
            4 = Credit Voucher
        """,
        example = "1",
        required = true,
        allowableValues = ["1", "2", "3", "4"]
    )
    val operationTypeId: Long?,

    @field:NotNull(message = "Amount is required")
    @field:Positive(message = "Amount must be positive")
    @Schema(
        description = "Transaction Amount",
        example = "50.00"
    )
    val amount: BigDecimal?
)

data class TransactionVO(
    val codAccount: String,
    val operationTypeId: Long,
    val amount: BigDecimal
)

data class TransactionResponse(
    @JsonProperty("transaction_code")
    val codTransaction: String,

    @JsonProperty("account_code")
    val codAccount: String,

    @JsonProperty("operation_type_id")
    val operationTypeId: Long,

    val amount: BigDecimal,

    @JsonProperty("event_date")
    val eventDate: LocalDateTime
)