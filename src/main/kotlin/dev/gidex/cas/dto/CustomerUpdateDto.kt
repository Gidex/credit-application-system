package dev.gidex.cas.dto

import dev.gidex.cas.entity.Customer
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CustomerUpdateDto(
    @field:NotEmpty(message = "First Name should not be empty") val firstName: String,
    @field:NotEmpty(message = "Last Name should not be empty") val lastName: String,
    @field:NotNull(message = "Income should not be null") val income: BigDecimal,
    @field:NotEmpty(message = "Zip Code Name should not be empty") val zipCode: String,
    @field:NotEmpty(message = "Street Name should not be empty") val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street

        return customer
    }
}
