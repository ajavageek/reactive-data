package ch.frankel.blog.reactivedata.jooq

import java.time.LocalDate
import org.jooq.DSLContext
import org.jooq.Record5
import org.jooq.Record7
import org.jooq.impl.DSL
import reactor.core.publisher.Flux
import ch.frankel.blog.reactivedata.jooq.tables.references.PERSON
import ch.frankel.blog.reactivedata.jooq.tables.references.PERSON_ADDRESS
import io.r2dbc.spi.ConnectionFactory

data class Person(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate?,
)

data class Address(
    val id: Long,
    val firstLine: String,
    val secondLine: String?,
    val zip: String,
    val city: String,
    val state: String?,
    val country: String
)

data class PersonWithAddresses(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate?,
    val addresses: List<Address>
)

class JooqRepository(private val connectionFactory: ConnectionFactory) {

    private val ctx: DSLContext = DSL.using(connectionFactory).dsl()

    private val addressMapper = { it: Record7<Long?, String?, String?, String?, String?, String?, String?> ->
        Address(
            it.value1()!!,
            it.value2()!!,
            it.value3(),
            it.value4()!!,
            it.value5()!!,
            it.value6(),
            it.value7()!!
        )
    }

    private val personWithAddressesMapper = { it: Record5<Long?, String?, String?, LocalDate?, List<Address>> ->
        PersonWithAddresses(
            it.value1()!!,
            it.value2()!!,
            it.value3()!!,
            it.value4(),
            it.value5()
        )
    }

    fun findAll(): Flux<PersonWithAddresses> {
        val people = ctx.select(
            PERSON.ID,
            PERSON.FIRST_NAME,
            PERSON.LAST_NAME,
            PERSON.BIRTHDATE,
            DSL.multiset(
                DSL.select(
                    PERSON_ADDRESS.ADDRESS_ID,
                    PERSON_ADDRESS.address.FIRST_LINE,
                    PERSON_ADDRESS.address.SECOND_LINE,
                    PERSON_ADDRESS.address.ZIP,
                    PERSON_ADDRESS.address.CITY,
                    PERSON_ADDRESS.address.STATE,
                    PERSON_ADDRESS.address.COUNTRY,
                ).from(PERSON_ADDRESS)
                    .where(PERSON_ADDRESS.PERSON_ID.eq(PERSON.ID))
            )
            .convertFrom { it.map(addressMapper) }
        ).from(PERSON)
        return Flux.from(people).map(personWithAddressesMapper)
    }
}
