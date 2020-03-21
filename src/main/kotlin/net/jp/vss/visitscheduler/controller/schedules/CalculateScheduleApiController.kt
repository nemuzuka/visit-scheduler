package net.jp.vss.visitscheduler.controller.schedules

import net.jp.vss.visitscheduler.controller.exceptions.HttpBadRequestException
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCase
import net.jp.vss.visitscheduler.usecase.schedules.CalculateUseCaseResult
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * スケジュール計算.
 *
 * @property calculateUseCase Schedule 計算の UseCase
 */
@RestController
@RequestMapping("/api/schedules")
@Validated
class CalculateScheduleApiController(
    private val calculateUseCase: CalculateUseCase
) {

    companion object {
        private val log = LoggerFactory.getLogger(CalculateScheduleApiController::class.java)
    }

    /**
     * CalculateSchedule.
     *
     * @param parameter パラメータ
     * @return レスポンス
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], path = ["/_calculate"])
    fun calculateSchedule(
        @Validated
        @RequestBody
        parameter: CalculateScheduleApiParameter
    ): ResponseEntity<CalculateUseCaseResult> {
        try {
            val result = calculateUseCase.calculateSchedule(parameter.toParameter())
            return ResponseEntity.ok(result)
        } catch (e: IllegalStateException) {
            log.info("BadRequest Parameter({}) {}", parameter, e.message)
            throw HttpBadRequestException(e.message!!, e)
        } catch (e: IllegalArgumentException) {
            log.info("BadRequest Parameter({}) {}", parameter, e.message)
            throw HttpBadRequestException(e.message!!, e)
        } catch (e: CalculateUseCase.CalculateException) {
            log.info("BadRequest Parameter({}) {}", parameter, e.message)
            throw HttpBadRequestException(e.message!!, e)
        }
    }
}
