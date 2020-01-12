package net.jp.vss.visitscheduler.infrastructure.schools

import net.jp.vss.visitscheduler.domain.Attributes
import net.jp.vss.visitscheduler.domain.ResourceAttributes
import net.jp.vss.visitscheduler.domain.schools.School
import net.jp.vss.visitscheduler.domain.users.User
import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Table
import org.seasar.doma.Version

/**
 * School Entity.
 *
 * @property schoolId 学校識別子
 * @property schoolCode 学校コード
 * @property userCode ユーザコード
 * @property name 学校名
 * @property memo メモ
 * @property attributes 付帯情報
 * @property createUserCode 生成ユーザコード
 * @property createAt 生成日時
 * @property lastUpdateUserCode 最終更新ユーザコード
 * @property lastUpdateAt 最終更新日時
 * @property versionNo バージョン
 */
@Entity(immutable = true)
@Table(name = "schools")
data class SchoolEntity(
    @Id
    @Column(name = "school_id")
    val schoolId: String,

    @Column(name = "school_code")
    val schoolCode: String,

    @Column(name = "user_code")
    val userCode: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "memo")
    val memo: String?,

    @Column(name = "attributes")
    val attributes: String?,

    @Column(name = "create_user_code")
    val createUserCode: String,

    @Column(name = "create_at")
    val createAt: Long,

    @Column(name = "last_update_user_code")
    val lastUpdateUserCode: String,

    @Column(name = "last_update_at")
    val lastUpdateAt: Long,

    @Column(name = "version_no")
    @Version
    val versionNo: Long
) {
    /**
     * School 変換
     *
     * @return 変換後 School
     */
    fun toSchool(): School {
        val schoolId = School.SchoolId(schoolId)
        val schoolCode = School.SchoolCode(schoolCode)
        val userCode = User.UserCode(userCode)
        val schoolDetail = School.SchoolDetail(name = name,
            memo = memo,
            attributes = Attributes.of(attributes))
        val resourceAttributes = ResourceAttributes(createUserCode = createUserCode,
            createAt = createAt,
            lastUpdateUserCode = lastUpdateUserCode,
            lastUpdateAt = lastUpdateAt,
            version = versionNo)
        return School(schoolId = schoolId, schoolCode = schoolCode, userCode = userCode, schoolDetail = schoolDetail,
            resourceAttributes = resourceAttributes)
    }
}
