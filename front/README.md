# フロント側開発

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Run your tests
```
npm run test
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


## メモ

### Spring の resources に Vue.js を組み込む方法

```
$ npm run build
```

を実行すると、`../src/main/resources/static/` にファイルを出力します
`./gradlew build` の前に実行することで、生成 jar に Vue.js を組み込む事ができます

### axios を 0.18 系にしている理由
0.19 にしない理由は、OPTIONS メソッドの後の GET メソッドに Content-Type を付与しない為です。
(どうも、GET メソッドで Content-Type を指定しないことを axios は推奨している模様)


## ローカルで開発する

1. `./gradlew -is bootRun -Ppersonal` を実行して、API Server を起動しておきます(デフォルトは 8089)
2. (別のターミナルで) `$ npm run serve` を実行すると別の server が起動します(デフォルトは 13000)
3. ブラウザより `http://localhost:13000` にアクセスして動作確認します
    * Vue ファイルを変更すると即時反映します
