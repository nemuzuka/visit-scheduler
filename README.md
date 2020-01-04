# 学校訪問スケジューラー

## ローカル起動

初回だけ実行してください

```sh
$ cd src/main/resources
$ cp _sample_application-personal.properties application-personal.properties
```

コピーした `application-personal.properties` の情報(ex. RDBMS の接続先)を変更してください

起動は以下のコマンドを使用します

```sh
$  ./gradlew -is bootRun -Ppersonal
```

以下のような情報が出るまで待ってください(Started VisitSchedulerApplicationKt)
```sh
2020/01/04 16:27:45.026 [main] INFO  n.j.v.v.VisitSchedulerApplicationKt:61 - Started VisitSchedulerApplicationKt in 2.807 seconds (JVM running for 3.31)
```

起動した後、以下のコマンドを叩いてレスポンスがくれば起動成功です。

```sh
$ curl -H 'Content-Type:application/json' http://localhost:8089/api/health
```

```
It's work!
```

## IntelliJ の設定

### doma のアノテーションを有効にする

Preferences>Build, Excution, Deployment>Compiler>Annotation Processors

* Enable annotation processingをチェックする
* Store generated sources relative toをModule content rootに設定する

## ktlint

### フォーマット

push する前に実施することを想定

```sh
$ ./gradlew ktlintFormat
```

### ktlint のみ実行

CI 回す時にチェックする(エラーがある場合、CI が通らない)

```sh
$ ./gradlew ktlint
```
