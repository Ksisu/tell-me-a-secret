# Tell Me A Secret
[![Build Status](https://travis-ci.org/Ksisu/tell-me-a-secret.svg?branch=master)](https://travis-ci.org/Ksisu/tell-me-a-secret)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/Ksisu/tell-me-a-secret&style=flat)](https://mergify.io)
It's a microservice to store short-live-secrets in redis.

```
POST /api/secret
{
  "secret": "MySecret",
  "forgetAfter": 60,
  "readOnlyOnce": true
}
```
 - secret: the secret :)
 - forgetAfter: the secret max lifetime in seconds
 - readOnlyOnce: if enabled you can read the secret only once
 
Return example:
```
{"uuid": "a1d0f826-9d44-4158-8894-2f35026dbabc"}
```

---

```
GET /api/secret/<<UUID>>
```

Return example:
```
{"secret": "MySecret"}
```

---

How to run:
```
docker run \
  -e REDIS_HOST=redis.example.com \
  -e REDIS_PORT=6379 \
  -e CRYPTOR_SECRET=84AmEdqF7YTruR2o4^2BfwkSA0weHtEVgHz$7Rqy \
  -p 8080:8080 \
  ksisu/tell-me-a-secret
```

---

`make up` start redis on `localhost:6379`

`make down` stop redis and remove all data

`make redis` open redis-cli
