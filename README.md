#Tell Me A Secret
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

```
GET /api/secret/<<UUID>>
```

Return example:
```
{"secret": "MySecret"}
```

---

`make up` start redis on `localhost:6379`

`make down` stop redis and remove all data

`make redis` open redis-cli
