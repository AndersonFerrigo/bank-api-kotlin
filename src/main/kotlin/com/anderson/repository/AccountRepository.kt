package com.anderson.repository

import com.anderson.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, Long>{

    fun findByDocument(document: String) : Optional<Account>
}