Examples using S3
=================

# Deploy
```
cd deploy
(cd ..; mvn -DskipTests=true clean package); \
sls deploy -s test -r ap-northeast-1 -v
```

