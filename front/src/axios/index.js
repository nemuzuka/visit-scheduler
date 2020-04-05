// axios の共通設定
import Axios from 'axios'
import baseURL from '../url'
import Utils from '../utils'

const http = Axios.create({
  // for cors
  withCredentials: true,
  headers:{
    'Accept': 'application/json',
    'Content-Type': 'application/json;charset=UTF-8',
    'Access-Control-Allow-Credentials': true
  },
  params: {},
  data: {},
  baseURL: baseURL,
  responseType: 'json',
})

http.interceptors.request.use( (config) => {
  Utils.show("loader")
  return config
})

http.interceptors.response.use(
  (response) => {
    Utils.hide("loader")
    return response
  },
  (error) => {
    Utils.hide("loader")
    if (error.response.status === 401) {
      // 認証エラー時の処理
      window.location = "/#/login"
    } else if (error.response.status === 500) {
      // システムエラー時の処理
      window.location = "/#/error"
    }
    return Promise.reject(error)
  }
)

export default http
