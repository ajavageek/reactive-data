package ch.frankel.blog.reactivedata

import ch.frankel.blog.reactivedata.hibernate.HibernateRepository
import ch.frankel.blog.reactivedata.jooq.JooqRepository
import ch.frankel.blog.reactivedata.jooq.PersonWithAddresses
import ch.frankel.blog.reactivedata.springdata.PersonLoadOfficeListener
import ch.frankel.blog.reactivedata.springdata.PersonRepository
import io.r2dbc.spi.ConnectionFactory
import org.jooq.impl.DSL
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import ch.frankel.blog.reactivedata.hibernate.Person as HibernatePerson
import ch.frankel.blog.reactivedata.springdata.Person as SpringPerson


@SpringBootApplication
class ReactiveDataApplication

fun beans() = beans {
    bean {
        router {
            GET("/spring") {
                val repo = ref<PersonRepository>()
                ServerResponse.ok().body(repo.findAll(), SpringPerson::class.java)
            }
            GET("/jooq") {
                val repo = JooqRepository(ref<ConnectionFactory>())
                ServerResponse.ok().body(repo.findAll(), PersonWithAddresses::class.java)
            }
            GET("/hibernate") {
                val repo = HibernateRepository()
                ServerResponse.ok().body(repo.findAll(), HibernatePerson::class.java)
            }
        }
    }
    bean<PersonLoadOfficeListener>()
}

fun main(args: Array<String>) {
    runApplication<ReactiveDataApplication>(*args) {
        addInitializers(beans())
    }
}
