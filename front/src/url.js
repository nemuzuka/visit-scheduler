let baseURL = ''
if(process.env.NODE_ENV === 'test') {
  baseURL = 'http://localhost:8089' // バックエンドのURL:port を指定する
}

export default baseURL
