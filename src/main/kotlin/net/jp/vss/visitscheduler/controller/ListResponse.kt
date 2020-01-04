package net.jp.vss.visitscheduler.controller

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * List レスポンス.
 *
 * @param T 型
 * @property list レスポンス対象 List
 */
data class ListResponse <T> (
    @field:JsonProperty("elements")
    val list: List<T>
)
