package dev.gidex.cas.controller

import dev.gidex.cas.dto.CustomerDto
import dev.gidex.cas.dto.CustomerUpdateDto
import dev.gidex.cas.dto.CustomerView
import dev.gidex.cas.entity.Customer
import dev.gidex.cas.service.impl.CustomerService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerController(
    private val customerService: CustomerService
) {

    @PostMapping
    fun saveCustomer(@RequestBody customerDto: CustomerDto): String {
        val savedCustomer = this.customerService.save(customerDto.toEntity())
        return "Customer ${savedCustomer.email} saved!"
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): CustomerView {
        val customer = this.customerService.findById(id)

        return CustomerView(customer)
    }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long) = this.customerService.delete(id)

    @PatchMapping
    fun updateCustomer(@RequestParam(value = "customerId") id: Long,
                       @RequestBody customerUpdate: CustomerUpdateDto): CustomerView {
        val customer = this.customerService.findById(id)

        val customerToUpdate = customerUpdate.toEntity(customer)
        val customerUpdated = this.customerService.save(customerToUpdate)

        return CustomerView(customerUpdated)
    }
}