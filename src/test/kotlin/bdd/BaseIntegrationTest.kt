package bdd

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base test class for integration tests.
 *
 * NOTE: Testcontainers disabled - using local PostgreSQL instead.
 * Make sure PostgreSQL is running locally on port 5432.
 *
 * To enable Testcontainers (requires Docker):
 * 1. Uncomment @Testcontainers annotation
 * 2. Uncomment @Container and postgresContainer
 * 3. Start Docker Desktop
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestApplication::class]
)
@Testcontainers // one day ... maybe it'll work
abstract class BaseIntegrationTest {

    companion object {
         @Container
         val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15-alpine").apply {
             withDatabaseName("transactions_test_db")
             withUsername("test_user")
             withPassword("test_password")
             withReuse(true)
         }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            // Using local PostgreSQL instead of Testcontainers
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
            registry.add("spring.datasource.url") { "jdbc:postgresql://localhost:5432/transaction" }
            registry.add("spring.datasource.username") { "postgres" }
            registry.add("spring.datasource.password") { "postgres" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.jpa.show-sql") { "true" }
        }
    }
}