package ch.frankel.blog.reactivedata.springdata

import java.time.LocalDate
import org.springframework.context.annotation.Lazy
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.annotation.Transient
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.sql.SqlIdentifier
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux


data class Person(
    @Id val id: Long,
    val firstName: String,
    val lastName: String,
    val birthdate: LocalDate?,
    @Transient
    val addresses: MutableSet<Address> = mutableSetOf()
) {
    @PersistenceCreator
    constructor(
        id: Long,
        firstName: String,
        lastName: String,
        birthdate: LocalDate? = null
    ) : this(id, firstName, lastName, birthdate, mutableSetOf())
}

data class Address(
    @Id val id: Long,
    val firstLine: String,
    val secondLine: String?,
    val zip: String,
    val city: String,
    val state: String?,
    val country: String
)

interface PersonRepository : ReactiveCrudRepository<Person, Long>

interface AddressRepository : ReactiveCrudRepository<Address, Long> {

    @Query("SELECT * FROM ADDRESS WHERE ID IN (SELECT ADDRESS_ID FROM PERSON_ADDRESS WHERE PERSON_ID = :id)")
    fun findAddressForPersonById(id: Long): Flux<Address>
}

class PersonLoadOfficeListener(@Lazy private val repo: AddressRepository) : AfterConvertCallback<Person> {

    override fun onAfterConvert(person: Person, table: SqlIdentifier) =
        repo.findAddressForPersonById(person.id)
            .mapNotNull {
                person.addresses.add(it)
                person
            }.takeLast(1)
            .single(person)
}
