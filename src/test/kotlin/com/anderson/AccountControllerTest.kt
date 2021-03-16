package com.anderson

import com.anderson.model.Account
import com.anderson.repository.AccountRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var accountRepository: AccountRepository

    @Test
    fun `test find all` (){
        accountRepository.save(Account(name = "Anderson", document = "111234121234", phone = "11978651234"))
        accountRepository.save(Account(name = "Anastacia", document = "5467111234121234", phone = "11978651235"))
        accountRepository.save(Account(name = "João Antonio", document = "4433111234121234", phone = "11978651236"))
        accountRepository.save(Account(name = "Maria Desaparecida", document = "9988111234121234", phone = "11978651238"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test find by id` (){
       val  account = accountRepository.save(Account(name = "Anderson", document = "1234111234121234", phone = "11978651234"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/${account.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(account.id))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account` (){
        val  account = Account(name = "Anderson", document = "1234111234121234", phone = "11978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        Assertions.assertFalse(accountRepository.findAll().isEmpty())
    }

    @Test
    fun `test update account` (){
        val  account = accountRepository.save(
                    Account(name = "Anderson", document = "1234111234121234", phone = "11978651234")
        ).copy(name = "Anderson Updated")

        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/${account.id}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        val findById  = accountRepository.findById(account.id!!)
        Assertions.assertTrue(findById.isPresent)
        Assertions.assertEquals(account.name, findById.get().name)
    }

    @Test
    fun `test delete account` (){
        val  account = accountRepository.save(
            Account(name = "Anderson", document = "1234111234121234", phone = "11978651234")
        )

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/${account.id}")
        ).andExpect(MockMvcResultMatchers.status().isOk)
         .andDo(MockMvcResultHandlers.print())

        val findById  = accountRepository.findById(account.id!!)
        Assertions.assertFalse(findById.isPresent)

    }

    @Test
    fun `test create account validation error empty name` (){
        val  account = Account(name = "", document = "1234111234121234", phone = "11978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[nome] não pode estar em branco!"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `test create account validation error name should be 5 character` (){
        val  account = Account(name = "test", document = "1234111234121234", phone = "11978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[nome] deve conter ao minimo 5 cacarcteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty document` (){
        val  account = Account(name = "testedoc", document = "", phone = "11978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] não pode estar em branco!"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `test create account validation error document should be 11 character` (){
        val  account = Account(name = "testedoc", document = "1234567890", phone = "11978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] deve conter ao minimo 11 cacarcteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty phone` (){
        val  account = Account(name = "testedoc", document = "12345678901234", phone = "")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] não pode estar em branco!"))
            .andDo(MockMvcResultHandlers.print())

    }

    @Test
    fun `test create account validation error phone must be 11 character` (){
        val  account = Account(name = "testedoc", document = "123456789021", phone = "978651234")
        val json = ObjectMapper().writeValueAsString(account)
        accountRepository.deleteAll()

        mockMvc.perform(MockMvcRequestBuilders.post("/accounts")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] deve conter 11 cacarcteres!"))
            .andDo(MockMvcResultHandlers.print())
    }

}