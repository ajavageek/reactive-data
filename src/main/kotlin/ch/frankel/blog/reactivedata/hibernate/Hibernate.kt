package ch.frankel.blog.reactivedata.hibernate

import java.time.LocalDate
import io.smallrye.mutiny.converters.uni.UniReactorConverters
import jakarta.persistence.*
import org.hibernate.reactive.mutiny.Mutiny
import reactor.core.publisher.Mono

@Entity
@Table(name = "person", schema = "people")
class Person(
    @Id var id: Long?,
    @Column(name = "first_name")
    var firstName: String?,
    @Column(name = "last_name")
    var lastName: String?,
    var birthdate: LocalDate?,
    @ManyToMany
    @JoinTable(
        name = "person_address",
        schema = "people",
        joinColumns = [ JoinColumn(name = "person_id") ],
        inverseJoinColumns = [ JoinColumn(name = "address_id") ]
    )
    val addresses: MutableSet<Address> = mutableSetOf()
) {
    internal constructor() : this(null, null, null, null)
}

@Entity
@Table(name = "address", schema = "people")
data class Address(
    @Id var id: Long? = null,
    @Column(name = "first_line")
    var firstLine: String?,
    @Column(name = "second_line")
    var secondLine: String?,
    var zip: String?,
    var city: String?,
    var state: String?,
    var country: String?
) {
    internal constructor() : this(null, null, null, null, null, null, null)
}

class HibernateRepository {

    private val sessionFactory = Persistence
        .createEntityManagerFactory("postgresql")
        .unwrap(Mutiny.SessionFactory::class.java)

    fun findAll(): Mono<MutableList<Person>> {
        return sessionFactory.withSession {
            it.createQuery<Person>("SELECT p FROM Person p LEFT JOIN FETCH p.addresses a").resultList
        }.convert()
         .with(UniReactorConverters.toMono())
    }
}
