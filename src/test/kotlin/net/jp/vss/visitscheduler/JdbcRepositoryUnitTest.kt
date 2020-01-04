package net.jp.vss.visitscheduler

import org.flywaydb.test.FlywayTestExecutionListener
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional

/**
 * JDBC を使用した Repository のテストクラスを意味する annotation
 */
@TestExecutionListeners(
    DependencyInjectionTestExecutionListener::class,
    TransactionalTestExecutionListener::class,
    FlywayTestExecutionListener::class
)
@TestPropertySource("/application-unittest.properties")
@Transactional
@Rollback
@SpringBootTest
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdbcRepositoryUnitTest
