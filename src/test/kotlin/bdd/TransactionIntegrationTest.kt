package bdd

import com.transaction.api.AccountRequest
import com.transaction.api.TransactionRequest
import com.transaction.persistence.AccountRepository
import com.transaction.persistence.TransactionRepository
import com.transaction.domain.transaction.enum.OperationType
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import java.math.BigDecimal
import java.util.UUID

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TransactionIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    private var testAccountId: String = ""

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.basePath = ""

        transactionRepository.deleteAll()
        accountRepository.deleteAll()

        testAccountId = Given {
            contentType(ContentType.JSON)
            body(AccountRequest(documentNumber = "12345678900"))
        } When {
            post("/account")
        } Then {
            statusCode(201)
        } Extract {
            path("account_code")
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should create transaction with negative amount for purchase")
    fun `should create transaction with negative amount for purchase`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = 1L,
            amount = BigDecimal("50.00")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(201)
            body("transaction_code", notNullValue())
            body("account_code", equalTo(testAccountId))
            body("operation_type_id", equalTo(1))
            body("amount", equalTo(-50.00f))
            body("event_date", notNullValue())
        }
    }

    @Test
    @Order(2)
    @DisplayName("Should create transaction with positive amount for credit")
    fun `should create transaction with positive amount for credit`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = OperationType.CREDIT_VOUCHER.id,
            amount = BigDecimal("60.00")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(201)
            body("amount", equalTo(60.00f))
        }
    }

    @Test
    @Order(3)
    @DisplayName("Should create purchase with installments")
    fun `should create purchase with installments`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = OperationType.PURCHASE_INSTALLMENTS.id,
            amount = BigDecimal("23.50")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(201)
            body("operation_type_id", equalTo(2))
            body("amount", equalTo(-23.50f))
        }
    }

    @Test
    @Order(4)
    @DisplayName("Should create withdrawal")
    fun `should create withdrawal`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = OperationType.WITHDRAWAL.id,
            amount = BigDecimal("18.70")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(201)
            body("operation_type_id", equalTo(3))
            body("amount", equalTo(-18.70f))
        }
    }

    @Test
    @Order(5)
    @DisplayName("Should return 404 for non-existent account")
    fun `should return 404 for non-existent account`() {
        val accountId = UUID.randomUUID().toString()
        val request = TransactionRequest(
            codAccount = accountId,
            operationTypeId = 1L,
            amount = BigDecimal("50.00")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(404)
            body("status", equalTo(404))
            body("message", containsString("Account with ID $accountId not found"))
        }
    }

    @Test
    @Order(6)
    @DisplayName("Should return 404 for invalid operation type")
    fun `should return 404 for invalid operation type`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = 999L,
            amount = BigDecimal("50.00")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(404)
            body("status", equalTo(404))
            body("message", containsString("Operation type with ID 999 not found"))
        }
    }

    @Test
    @Order(7)
    @DisplayName("Should return 400 for negative amount")
    fun `should return 400 for negative amount`() {
        val request = mapOf(
            "account_id" to testAccountId,
            "operation_type_id" to 1,
            "amount" to -50.00
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(400)
            body("status", equalTo(400))
        }
    }


    @Test
    @Order(8)
    @DisplayName("Should handle decimal precision correctly")
    fun `should handle decimal precision correctly`() {
        val request = TransactionRequest(
            codAccount = testAccountId,
            operationTypeId = 1L,
            amount = BigDecimal("123.45")
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/transaction")
        } Then {
            statusCode(201)
            body("amount", equalTo(-123.45f))
        }
    }

}