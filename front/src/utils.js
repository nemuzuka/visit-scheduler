import Moment from 'moment'
import Markd from 'marked'

export default class Utils {
  /**
   * ダイアログOpen,
   * @param {String} id ID要素.
   */
  static openDialog(id) {
    document.querySelector('html').classList.add('is-clipped');
    document.getElementById(id).classList.add('is-active');
  }

  /**
   * ダイアログClose.
   * @param {String} id ID要素
   */
  static closeDialog(id) {
    document.querySelector('html').classList.remove('is-clipped');
    document.getElementById(id).classList.remove('is-active');
  }

  /**
   * 日付文字列変換.
   * @param epochMillis 対象時間(Epoc ミリ秒)
   */
  static dateToString(epochMillis) {
    if(epochMillis === null) {
      return ""
    }
    const moment = Moment(epochMillis)
    return moment.format("YYYY-MM-DD")
  }

  static toMarkdown(src) {
    if(src == null) {
      return ""
    }
    return Markd(src)
  }

  /**
   * localStrage より取得.
   * @param key キー
   * @return JSON オブジェクト(存在しない場合、null)
   */
  static getLocalStorage(key) {
    const jsonString = localStorage.getItem(key)
    return jsonString === null ? null : JSON.parse(jsonString)
  }

  /**
   * localStorage に設定.
   * @param key キー
   * @param data 設定オブジェクト
   */
  static setLocalStorage(key, data) {
    const jsonString = JSON.stringify(data)
    localStorage.setItem(key, jsonString)
  }

  /**
   * 表示.
   * @param id ID要素
   */
  static show(id) {
    document.getElementById(id).style.display ="block"
  }

  /**
   * 非表示.
   * @param id ID要素
   */
  static hide(id) {
    document.getElementById(id).style.display ="none"
  }

  /**
   * 表示用 targetYearAndMonth 文字列生成.
   * @param targetYearAndMonth 対象年月文字列(YYYY-MM 形式)
   * @return string 表示用文字列
   */
  static targetYearAndMonthForView(targetYearAndMonth) {
    const result = targetYearAndMonth.split('-')
    return result[0] + "年" + parseInt(result[1]) + "月"
  }

  static getWeekdayString(targetDate) {
    switch(targetDate.format('d')) {
      case '0':
        return '日'
      case '1':
        return '月'
      case '2':
        return '火'
      case '3':
        return '水'
      case '4':
        return '木'
      case '5':
        return '金'
      case '6':
        return '土'
      default:
        return ''
    }
  }
}
