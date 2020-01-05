import Moment from 'moment'

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

}
