package com.transaction.api

import com.transaction.domain.account.AccountService
import com.transaction.domain.account.AccountVO
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag


@RestController
@RequestMapping("/account")
@Tag(
    name = "Accounts",
    description = "Endpoints for customer account management"
)
class AccountEndpoint(
    private val accountService: AccountService
) {

    @PostMapping
    @Operation(
        summary = "Create new account",
        description = "Creates a new customer account with a unique document number"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Account created successfully",
                content = [Content(schema = Schema(implementation = AccountResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid data",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Account with this document already exists",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun createAccount(
        @Valid @RequestBody request: AccountRequest
    ): ResponseEntity<AccountResponse> {
        val accountVO = AccountVO(request.documentNumber)
        val accountResponseVO = accountService.createAccount(accountVO)
        val response = AccountResponse(
            codAccount = accountResponseVO.codAccount,
            documentNumber = accountResponseVO.documentNumber
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{codAccount}")
    @Operation(
        summary = "Get account by ID",
        description = "Returns the details of a specific account"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Account found",
                content = [Content(schema = Schema(implementation = AccountResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Account not found",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))]
            )
        ]
    )
    fun getAccount(
        @PathVariable codAccount: String
    ): ResponseEntity<AccountResponse> {
        val accountResponseVO = accountService.getAccount(codAccount)
        val response = AccountResponse(
            codAccount = accountResponseVO.codAccount,
            documentNumber = accountResponseVO.documentNumber
        )
        return ResponseEntity.ok(response)
    }

}

data class AccountRequest(
    @field:NotBlank(message = "Document number is required")
    @field:Pattern(
        regexp = "^[A-Za-z0-9]+$",
        message = "Document number must contain only letters and numbers"
    )
    @field:Size(min = 1, max = 14, message = "length must be lower than 14 and higher than 1")
    @JsonProperty("document_number")
    val documentNumber: String
)

data class AccountResponse(
    @JsonProperty("account_code")
    val codAccount: String,

    @JsonProperty("document_number")
    val documentNumber: String
)