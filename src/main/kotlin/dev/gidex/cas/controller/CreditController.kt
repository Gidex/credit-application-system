package dev.gidex.cas.controller

import dev.gidex.cas.dto.CreditDto
import dev.gidex.cas.dto.CreditView
import dev.gidex.cas.dto.SimpleCreditView
import dev.gidex.cas.service.impl.CreditService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController("/api/credits")
class CreditController(private val creditService: CreditService) {

    @PostMapping
    fun saveCredit(@RequestBody creditDto: CreditDto): String {
        val credit = this.creditService.save(creditDto.toEntity())
        return "Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!"
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): List<SimpleCreditView> {

        return this.creditService.findAllByCustomerId(customerId).map { SimpleCreditView(it) }

        /*return this.creditService.findAllByCustomerId(customerId).stream()
            .map { CreditView(it) }
            .collect(Collectors.toList())*/
    }

    @GetMapping()
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long,
                         @PathVariable creditCode: UUID): CreditView {
        val credit = this.creditService.findByCreditCode(customerId, creditCode)

        return CreditView(credit)
    }

}