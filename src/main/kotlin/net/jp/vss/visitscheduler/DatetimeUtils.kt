package net.jp.vss.visitscheduler

/**
 * 日時ユーティリティ.
 */
class DatetimeUtils {

    companion object {
        /** ダミー時刻リソース. */
        private var dummyDatetimeResource: Long? = null

        /**
         * 現在時刻取得.
         *
         * @return 現在時刻(Unix Epoch milli)
         */
        fun now(): Long = dummyDatetimeResource ?: System.currentTimeMillis()

        /**
         * ダミー時刻リソース設定.
         *
         * テスト時にのみ使用してください
         *
         * @param dummyDatetimeResource ダミー時刻(Unix Epoch milli)
         */
        fun setDummyDatetimeResource(dummyDatetimeResource: Long) {
            this.dummyDatetimeResource = dummyDatetimeResource
        }

        /**
         * ダミー時刻リソースクリア.
         */
        fun clearDummyDatetimeResource() {
            dummyDatetimeResource = null
        }
    }
}
