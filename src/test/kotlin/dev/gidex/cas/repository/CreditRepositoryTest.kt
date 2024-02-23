package dev.gidex.cas.repository

import dev.gidex.cas.entity.Address
import dev.gidex.cas.entity.Credit
import dev.gidex.cas.entity.Customer
import dev.gidex.cas.enummeration.Status
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired
    lateinit var creditRepository: CreditRepository
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit
    @BeforeEach
    fun setup() {
        customer = testEntityManager.persist(buildCustomer())
        credit1 = testEntityManager.persist(buildCredit(customer = customer))
        credit2 = testEntityManager.persist(buildCredit(customer = customer))
    }

    @Test
    fun `should find credit by credit code`() {
        //given
        val creditCode1 = UUID.fromString("4a5238c5-fa8d-4b24-b9dd-faf9fae5eeab")
        val creditCode2 = UUID.fromString("bb7ae880-dc86-448f-b305-e11cc27bd42b")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2
        //when
        val fakeCredit1 = creditRepository.findByCreditCode(creditCode1)
        val fakeCredit2 = creditRepository.findByCreditCode(creditCode2)
        //then
        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isNotNull
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
    }

    @Test
    fun `should find all credits by customer id`() {
        //given
        val customerId = 1L
        //when
        val creditList = creditRepository.findAllByCustomerId(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
    }

    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(500.0),
        firstInstallmentDay: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 5,
        customer: Customer = buildCustomer()
    ) = Credit(
        creditValue = creditValue,
        firstInstallmentDay = firstInstallmentDay,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )

    private fun buildCustomer(
        firstName: String = "Gidex",
        lastName: String = "Santana",
        cpf: String = "551.927.600-54",
        email: String = "gidex@mail.com",
        password: String = "123456",
        zipCode: String = "12375000",
        street: String = "Fool street",
        income: BigDecimal = BigDecimal.valueOf(1000.0)
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(zipCode = zipCode, street = street),
        income = income
    )
}