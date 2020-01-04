package net.jp.vss.visitscheduler.infrastructure.users

import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table

/**
 * OpenID Connect 認証情報 Entity.
 *
 * @property authenticatedPrincipalId 識別子
 * @property principal ユーザが認証した AuthorizedClientRegistration 上の principal
 * @property authorizedClientRegistrationId ユーザが認証した AuthorizedClientRegistrationId (e.g. google)
 */
@Entity(immutable = true)
@Table(name = "authenticated_principals")
data class AuthenticatedPrincipalEntity(
    @Id
    @Column(name = "authenticated_principal_id")
    val authenticatedPrincipalId: String,

    @Column(name = "principal")
    val principal: String,

    @Column(name = "authorized_client_registration_id")
    val authorizedClientRegistrationId: String
)
