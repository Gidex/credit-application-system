package dev.gidex.cas.dto

import dev.gidex.cas.entity.Credit
import java.math.BigDecimal
import java.util.UUID

data class SimpleCreditView(
    val creditCode: UUID,
    val creditValue: BigDecimal,
    val numberOfInstallments: Int
) {
    constructor(credit: Credit): this(
        creditCode = credit.creditCode,
        creditValue = credit.creditValue,
        numberOfInstallments = credit.numberOfInstallments
    )
}
