package com.transaction.persistence

import com.transaction.domain.transaction.enum.OperationType
import jakarta.persistence.Entity
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transaction")
data class TransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_transaction")
    val id: Long? = null,

    @Column(name = "cod_transaction")
    val codTransaction: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idt_account", nullable = false)
    val account: AccountEntity,

    @EnumeratedValue
    @Column(name = "idt_operation")
    val operationType: OperationType,

    @Column(name = "val_amount", nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Column(name = "dat_event", nullable = false)
    val datEvent: LocalDateTime = LocalDateTime.now(),

    @Column(name = "dat_creation", nullable = false)
    val datCreation: LocalDateTime = LocalDateTime.now()
)

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, Long> {
}