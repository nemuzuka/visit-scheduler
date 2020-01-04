SELECT
    u.*
FROM
    authenticated_principals ap
    INNER JOIN users u ON ap.authenticated_principal_id = u.authenticated_principal_id
WHERE
    ap.principal = /*principal*/'abc'
    AND ap.authorized_client_registration_id = /*authorizedClientRegistrationId*/'abc'
