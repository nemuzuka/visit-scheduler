package net.jp.vss.visitscheduler.domain.schools

import net.jp.vss.visitscheduler.domain.exceptions.DuplicateException
import net.jp.vss.visitscheduler.domain.exceptions.NotFoundException
import net.jp.vss.visitscheduler.domain.users.User

/**
 * School のリポジトリ.
 */
interface SchoolRepository {
    /**
     * 登録.
     *
     * @param school 対象 School
     * @return 登録後 School
     * @throws DuplicateException 既に存在する
     */
    fun createSchool(school: School): School

    /**
     * 取得.
     *
     * @param schoolCode 学校コード
     * @param userCode ユーザコード
     * @return 該当 School
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun getSchool(schoolCode: School.SchoolCode, userCode: User.UserCode): School

    /**
     * lock して取得.
     *
     * @param schoolCode 学校コード
     * @param userCode ユーザコード
     * @return 該当 School
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun lockSchool(schoolCode: School.SchoolCode, userCode: User.UserCode): School

    /**
     * 更新.
     *
     * version をインクリメントして School を更新します
     * @param school 対象 School
     * @return 更新後 School
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun updateSchool(school: School): School

    /**
     * 削除.
     *
     * School を削除します
     * @param school 対象 School
     * @throws NotFoundException 該当レコードが存在しない
     */
    fun deleteSchool(school: School)

    /**
     * 全件取得.
     *
     * @param userCode ユーザコード
     * @return 該当レコード
     */
    fun allSchools(userCode: User.UserCode): List<School>
}
