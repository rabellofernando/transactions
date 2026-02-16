package bdd

import com.transaction.api.AccountRequest
import com.transaction.persistence.AccountRepository
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

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DisplayName("Account Integration Tests")
class AccountIntegrationTest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.basePath = ""
        accountRepository.deleteAll()
    }

    @Test
    @Order(1)
    @DisplayName("Should create account successfully")
    fun `should create account successfully`() {
        val request = AccountRequest(documentNumber = "12345678900")

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(201)
            body("account_code", notNullValue())
            body("document_number", equalTo("12345678900"))
        }
    }

    @Test
    @Order(2)
    @DisplayName("Should get account by ID")
    fun `should get account by ID`() {
        val request = AccountRequest(documentNumber = "98765432100")

        val accountId = Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(201)
        } Extract {
            path<String>("account_code")
        }

        Given {
            contentType(ContentType.JSON)
        } When {
            get("/account/$accountId")
        } Then {
            statusCode(200)
            body("account_code", equalTo(accountId))
            body("document_number", equalTo("98765432100"))
        }
    }

    @Test
    @Order(3)
    @DisplayName("Should return 404 for non-existent account")
    fun `should return 404 for non-existent account`() {
        When {
            get("/account/{id}", "9999919")
        } Then {
            statusCode(404)
            body("status", equalTo(404))
            body("error", equalTo("Not Found"))
            body("message", containsString("Account with ID 9999919 not found"))
        }
    }

    @Test
    @Order(4)
    @DisplayName("Should return 409 for duplicate document number")
    fun `should return 409 for duplicate document number`() {
        val request = AccountRequest(documentNumber = "11111111111")

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(201)
        }

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(409)
            body("status", equalTo(409))
            body("error", equalTo("Conflict"))
            body("message", containsString("already exists"))
        }
    }

    @Test
    @Order(5)
    @DisplayName("Should return 400 for invalid document number")
    fun `should return 400 for invalid document number`() {
        val request = mapOf("document_number" to " ")

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(400)
            body("status", equalTo(400))
            body("error", equalTo("Validation Failed"))
        }
    }

    @Test
    @Order(6)
    @DisplayName("Should handle special characters in document number")
    fun `should handle special characters in document number`() {
        val request = AccountRequest(documentNumber = "123.456.789-00")

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/account")
        } Then {
            statusCode(400)
            body("message", equalTo("Invalid request parameters"))
        }
    }

}