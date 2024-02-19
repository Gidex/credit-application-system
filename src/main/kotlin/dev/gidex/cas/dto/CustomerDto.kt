package dev.gidex.cas.dto

import dev.gidex.cas.entity.Address
import dev.gidex.cas.entity.Customer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "First Name should not be empty") val firstName: String,
    @field:NotEmpty(message = "Last Name should not be empty") val lastName: String,
    @field:NotEmpty(message = "CPF Name should not be empty") @field:CPF(message = "Invalid CPF") val cpf: String,
    @field:NotEmpty(message = "Email Name should not be empty") @field:Email(message = "Invalid Email") val email: String,
    @field:NotNull(message = "Income should not be null") val income: BigDecimal,
    @field:NotEmpty(message = "Password Name should not be empty") val password: String,
    @field:NotEmpty(message = "Zip Code Name should not be empty") val zipCode: String,
    @field:NotEmpty(message = "Street Name should not be empty") val street: String
) {
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(zipCode = this.zipCode, street = this.street)
    )
}