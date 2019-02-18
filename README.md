# voice-to-text-line-bot

## 概要
LINE BOTです。  
ボイスメッセージ機能を使ってボイスメッセージを送信すると  
音声認識をして、テキストで返却します。


### localで動かす方法 (開発中など)

local環境を外部に公開するためにngrokをインストールします。
```
> brew cask install ngrok
> ngrok http 9000


(略)

Forwarding      https://0f91eaa9.ngrok.io -> localhost:9000

(略)

```

ここの`https://XXXXXXXX.ngrok.io`を
Messaging APIのWebhook URLの所に設定します。

アプリケーションを同じポートで起動します。

```
// application.yml

server:
  port: 9000
```


### 環境変数

以下の環境変数を設定すれば動くと思います。

* LINE_BOT_CHANNEL_SECRE
* LINE_BOT_CHANNEL_TOKEN
* GOOGLE_APPLICATION_CREDENTIALS

GOOGLE_APPLICATION_CREDENTIALSは以下を参考にしてください。
```
https://cloud.google.com/speech-to-text/?hl=ja
```

今回は"Cloud Speech-to-Text" APIを使用しています。
APIを使用するための登録手順は省略します。