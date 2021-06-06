# このアプリケーションは？

スクールを探しているユーザと、自分のスクールを紹介したいクライアントを結びつけるアプリケーションです。

クライアントはアカウントを作成すれば自分のスクールを登録することができ、ユーザは登録されたクライアントの中から自分の希望条件に合ったスクールを検索することができます。

[＞公開サイト](https://dktsearch.herokuapp.com/)

※Herokuのフリープランを使用しているため、スリープからの起動に少し時間がかかります

※クライアントの新規登録は現在無効化しています

# What is this?

This application connects user who want go culture school and client who introduced own school.

The client can register own school, if get account.

The user can search school which match his desire from registered by client.

[＞Site](https://dktsearch.herokuapp.com/)

※Since this application use free plan of heroku, it will take little time to wake up from sleep

※New client registration is currently disabled

## 機能

### ユーザの機能

* スクール検索

### クライアントの機能

* アカウント作成(メール認証)

* スクール情報登録

* 画像投稿(3Dカルーセル表示)

## Features

### User features

* Search for school

### Client features

* Get Account(authentication by mail)

* Register for information of schools

* Upload some photos(displayed by 3D_carousel)

## 進捗・課題

テスト：	ブラックボックステストのみ

## Progress・Task

Test：	Only black mox test

# ～Tools～

View：	HTML, CSS, javascript, thymeleaf, bootstrap

Core：	Java, SpringBoot

DB：		Postgresql

Picture storage：		AWS S3

Deploy：		heroku
