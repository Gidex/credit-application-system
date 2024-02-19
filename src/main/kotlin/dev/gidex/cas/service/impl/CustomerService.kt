package dev.gidex.cas.service.impl

import dev.gidex.cas.entity.Customer
import dev.gidex.cas.exception.BusinessException
import dev.gidex.cas.repository.CustomerRepository
import dev.gidex.cas.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer = this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow {
        throw BusinessException("Id $id not found")
    }

    override fun delete(id: Long) {
        val customer = this.findById(id)
        this.customerRepository.delete(customer)
    }
}