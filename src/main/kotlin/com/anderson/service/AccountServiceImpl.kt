package com.anderson.service

import com.anderson.model.Account
import com.anderson.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import java.util.*

@Service
class AccountServiceImpl (private val accountRepository: AccountRepository): AccountService {

    override fun create(account: Account): Account {
        Assert.hasLength(account.name, "[nome] não pode estar em branco!")
        Assert.isTrue(account.name.length >= 5, "[nome] deve conter ao minimo 5 caracteres!")

        Assert.hasLength(account.document, "[document] não pode estar em branco!")
        Assert.isTrue(account.document.length >= 11, "[document] deve conter ao minimo 11 caracteres!")

        Assert.hasLength(account.phone, "[phone] não pode estar em branco!")
        Assert.isTrue(account.phone.length == 11, "[phone] deve conter 11 caracteres!")

        return accountRepository.save(account)
    }

    override fun getAll(): List<Account> {
        return accountRepository.findAll()
    }

    override fun getById(id: Long): Optional<Account> {
        return accountRepository.findById(id)
    }

    override fun update(id: Long, account: Account): Optional<Account> {
        val optional =  getById(id)
        if(optional.isPresent){
            return optional.map {
                val accountToUpdate = it.copy(
                    name = account.name,
                    document = account.document,
                    phone = account.phone
                )
                accountRepository.save(accountToUpdate)
            }

        }
        return optional
    }

    override fun delete(id: Long) {
        accountRepository.findById(id).map {
            accountRepository.delete(it)
        }.orElseThrow{throw RuntimeException ("Id not found $id")}
    }
}