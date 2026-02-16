package com.transaction.persistence

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.Optional

@Entity
@Table(name = "account")
data class AccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idt_account")
    val id: Long? = null,

    @Column(name = "cod_account", nullable = false, unique = true)
    val codAccount: String,

    @Column(name = "val_document", nullable = false, unique = true, length = 14)
    val documentNumber: String,

    @OneToMany(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactionEntities: MutableList<TransactionEntity> = mutableListOf(),

    @Column(name = "dat_creation", nullable = false)
    val datCreation: LocalDateTime = LocalDateTime.now()

)

@Repository
interface AccountRepository : JpaRepository<AccountEntity, Long> {
    fun existsByDocumentNumber(documentNumber: String): Boolean

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
//    @Transactional(propagation = Propagation.MANDATORY)
    //maybe cache here? we'll see @Cacheable
    fun findByCodAccount(codAccount: String): Optional<AccountEntity>
}