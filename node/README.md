Node
====

```
$ cd deploy/s3
$ sls deploy -s <stage> -v
$ cd ../..
$ cd app
$ npm i
$ tsc; BUCKET_NAME=<YOUR BUCKET NAME> node target/genkeypair.js
```
