package dev.gidex.cas.service

import dev.gidex.cas.entity.Credit
import java.util.UUID

interface ICreditService {
    fun save(credit: Credit): Credit

    fun findAllByCustomerId(customerId: Long): List<Credit>

    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
}