package dev.gidex.cas.service

import dev.gidex.cas.entity.Address
import dev.gidex.cas.entity.Credit
import dev.gidex.cas.entity.Customer
import dev.gidex.cas.enummeration.Status
import dev.gidex.cas.exception.BusinessException
import dev.gidex.cas.repository.CreditRepository
import dev.gidex.cas.repository.CustomerRepository
import dev.gidex.cas.service.impl.CreditService
import dev.gidex.cas.service.impl.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import jakarta.persistence.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository
    @MockK
    @InjectMockKs
    lateinit var customerService: CustomerService

    @MockK
    lateinit var creditRepository: CreditRepository
    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun `should save credit`() {
        //given
        val fakeId = Random().nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)
        val fakeCredit = buildCredit(customer = fakeCustomer)

        every { customerService.findById(fakeId) } returns fakeCustomer
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual = creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun `should not save credit to invalid customer`() {
        //given
        val fakeId = Random().nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)
        val fakeCredit = buildCredit(customer = fakeCustomer)
        every { customerService.findById(fakeId) } throws BusinessException("Id $fakeId not found")
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.save(fakeCredit) }
            .withMessage("Id $fakeId not found")

        verify(exactly = 1) { customerService.findById(fakeId) }
    }

    @Test
    fun `should find all credits by customer id`() {
        //given
        val fakeCustomerId = Random().nextLong()
        val fakeCredit = buildCredit()
        val fakeCreditList = listOf(fakeCredit)
        every { creditRepository.findAllByCustomerId(fakeCustomerId) } returns fakeCreditList
        //when
        val actual = creditService.findAllByCustomerId(customerId = fakeCustomerId)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isSameAs(fakeCreditList)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeCustomerId) }
    }

    @Test
    fun `should find credit by credit code`() {
        //given
        val fakeCustomerId = Random().nextLong()
        val fakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = fakeCreditCode, customer = buildCustomer(id = fakeCustomerId))
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit
        //when
        val actual = creditService.findByCreditCode(fakeCustomerId, fakeCreditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should not find credit by invalid credit code`() {
        //given
        val fakeCustomerId = Random().nextLong()
        val fakeCreditCode = UUID.randomUUID()
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns null
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeCreditCode) }
            .withMessage("Credit code $fakeCreditCode not found")
    }

    @Test
    fun `should not find by credit code with invalid customer id`() {
        //given
        val fakeCustomerId = Random().nextLong()
        val fakeCustomerId2 = Random().nextLong()
        val fakeCreditCode = UUID.randomUUID()
        val fakeCredit = buildCredit(creditCode = fakeCreditCode, customer = buildCustomer(id = fakeCustomerId))
        every { creditRepository.findByCreditCode(fakeCreditCode) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId2, fakeCreditCode) }
            .withMessage("Contact admin")
    }

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.ZERO,
        firstInstallmentDay: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 5,
        status: Status = Status.IN_PROGRESS,
        customer: Customer = buildCustomer(),
        id: Long? = 1L
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        firstInstallmentDay = firstInstallmentDay,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id
    )

    private fun buildCustomer(
        firstName: String = "Gidex",
        lastName: String = "Santana",
        cpf: String = "551.927.600-54",
        email: String = "gidex@mail.com",
        password: String = "123456",
        zipCode: String = "12375000",
        street: String = "Fool street",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(zipCode = zipCode, street = street),
        income = income,
        id = id
    )
}