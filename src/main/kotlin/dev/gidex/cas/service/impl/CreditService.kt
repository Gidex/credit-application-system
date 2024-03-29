package dev.gidex.cas.service.impl

import dev.gidex.cas.entity.Credit
import dev.gidex.cas.exception.BusinessException
import dev.gidex.cas.repository.CreditRepository
import dev.gidex.cas.service.ICreditService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomerId(customerId: Long): List<Credit> =
        this.creditRepository.findAllByCustomerId(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = this.creditRepository.findByCreditCode(creditCode) ?: throw  BusinessException("Credit code $creditCode not found")

        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact admin")
    }
}