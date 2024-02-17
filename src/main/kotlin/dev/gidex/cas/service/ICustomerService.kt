package dev.gidex.cas.service

import dev.gidex.cas.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer

    fun findById(id: Long): Customer

    fun delete(id: Long)
}