package dev.gidex.cas.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gidex.cas.dto.CreditDto
import dev.gidex.cas.dto.CustomerDto
import dev.gidex.cas.entity.Credit
import dev.gidex.cas.entity.Customer
import dev.gidex.cas.repository.CreditRepository
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
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditControllerTest {
    @Autowired
    private lateinit var customerRepository: CustomerRepository
    @Autowired
    private lateinit var creditRepository: CreditRepository
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @Test
    fun `should save credit and return 200 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val creditDto = buildCreditDto(customerId = customer.id!!)
        val valueAsString = objectMapper.writeValueAsString(creditDto)
        //then
        //when
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save credit within invalid customer id and return 400 status`() {
        //given
        val creditDto = buildCreditDto()
        val valueAsString = objectMapper.writeValueAsString(creditDto)
        //then
        //when
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
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
    fun `should find all by customer id and return 200 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("${URL}?customerId=${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[1]").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find all by invalid customer id and return 400 status`() {
        //given
        val invalidCustomerId = 1L
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("${URL}?customerId=${invalidCustomerId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find by credit code and return 200 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit = creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("${URL}/${credit.creditCode}?customerId=${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(credit.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(credit.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(credit.status.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find by invalid credit code and return 400 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val invalidCreditCode = UUID.fromString("4a5238c5-fa8d-4b24-b9dd-faf9fae5eeab")
        //val credit = creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("${URL}/${invalidCreditCode}?customerId=${customer.id}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad request! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class dev.gidex.cas.exception.BusinessException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.*").value("Credit code 4a5238c5-fa8d-4b24-b9dd-faf9fae5eeab not found"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find by credit code within invalid customer id and return 400 status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val credit = creditRepository.save(buildCreditDto(customerId = customer.id!!).toEntity())
        val invalidCustomerId = 2L
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("${URL}/${credit.creditCode}?customerId=${invalidCustomerId}")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(MockMvcResultMatchers.jsonPath("$.exception").value(
                "class java.lang.IllegalArgumentException"
            ))
            .andExpect(MockMvcResultMatchers.jsonPath("$.details.*").value("Contact admin"))
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        firstInstallmentDay: LocalDate = LocalDate.parse("2024-07-10"),
        numberOfInstallments: Int = 5,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        firstInstallmentDay = firstInstallmentDay,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

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
}