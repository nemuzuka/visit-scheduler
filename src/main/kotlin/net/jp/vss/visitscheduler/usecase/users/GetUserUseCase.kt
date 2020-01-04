package net.jp.vss.visitscheduler.usecase.users

/**
 * User を取得する UseCase.
 */
interface GetUserUseCase {

    /**
     * User 取得.
     *
     * @param authorizedClientRegistrationId AuthorizedClientRegistrationId
     * @param principal Principal
     * @return 該当ユーザ(存在しない場合、null)
     */
    fun getUser(authorizedClientRegistrationId: String, principal: String): UserUseCaseResult?
}
