package dev.gidex.cas.controller

import dev.gidex.cas.dto.CreditDto
import dev.gidex.cas.dto.CreditView
import dev.gidex.cas.dto.SimpleCreditView
import dev.gidex.cas.service.impl.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/credits")
class CreditController(private val creditService: CreditService) {

    @PostMapping
    fun saveCredit(@RequestBody creditDto: CreditDto): ResponseEntity<String> {
        println(creditDto)
        val credit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.email} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<SimpleCreditView>> {

        val creditViewList = this.creditService.findAllByCustomerId(customerId).map { SimpleCreditView(it) }
        /*this.creditService.findAllByCustomerId(customerId).stream()
            .map { CreditView(it) }
            .collect(Collectors.toList())*/

        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long,
                         @PathVariable creditCode: UUID): ResponseEntity<CreditView> {
        val credit = this.creditService.findByCreditCode(customerId, creditCode)

        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }

}