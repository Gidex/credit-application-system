package dev.gidex.cas.dto

import dev.gidex.cas.entity.Credit
import dev.gidex.cas.entity.Customer
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Credit Value should not be null") val creditValue: BigDecimal,
    @field:Future(message = "Invalid date for the first installment") val firstInstallmentDay: LocalDate,
    @field:Min(value = 3, message = "The number of installments should be greater than 3")
    @field:Max(value = 12, message = "The number of installments should be lower than 12")
    val numberOfInstallments: Int,
    @field:NotNull(message = "Customer id should not be null") val customerId: Long
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        firstInstallmentDay = this.firstInstallmentDay,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
