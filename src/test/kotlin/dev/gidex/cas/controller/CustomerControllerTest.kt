package dev.gidex.cas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gidex.cas.dto.CustomerDto
import dev.gidex.cas.dto.CustomerUpdateDto
import dev.gidex.cas.entity.Address
import dev.gidex.cas.entity.Customer
import dev.gidex.cas.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should save customer`() {
        //giver
        val customerDto = buildCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Gidex"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Santana"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("551.927.600-54"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gidex@mail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12375000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Fool street"))
            //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save customer with same CPF and return 409 status`() {
        //given
        customerRepository.save(buildCustomerDto().toEntity())
        val customerDto = buildCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class org.springframework.dao.DataIntegrityViolationException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save customer with empty names and return 400 status`() {
        //given
        val customerDto = buildCustomerDto(firstName = "", lastName = "")
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class org.springframework.web.bind.MethodArgumentNotValidException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Gidex"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Santana"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("551.927.600-54"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gidex@mail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12375000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Fool street"))
            //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find customer within invalid id and return 400 status`() {
        //given
        val invalidId = 2L
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/$invalidId")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class dev.gidex.cas.exception.BusinessException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id and return 204 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer within invalid id and return 400 status`() {
        //given
        val invalidId = 2L
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/$invalidId")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class dev.gidex.cas.exception.BusinessException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
    }

    @Test
    fun `should update customer and return 200 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Gidexx"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Santana"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("551.927.600-54"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gidex@mail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("12375000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Fool street"))
            //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update customer within invalid id and return 400 status`() {
        //given
        val invalidId = 1L
        val customerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class dev.gidex.cas.exception.BusinessException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
    }

    private fun buildCustomerDto(
        firstName: String = "Gidex",
        lastName: String = "Santana",
        cpf: String = "551.927.600-54",
        email: String = "gidex@mail.com",
        password: String = "123456",
        zipCode: String = "12375000",
        street: String = "Fool street",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        zipCode = zipCode,
        street = street,
        income = income
    )

    private fun buildCustomerUpdateDto(
        firstName: String = "Gidexx",
        lastName: String = "Santana",
        zipCode: String = "12375000",
        street: String = "Fool street",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ) = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        zipCode = zipCode,
        street = street,
        income = income
    )
}