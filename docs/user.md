# User API Spec

## Register User

Endpoint : POST /api/users

Request Body :

```json
{
  "username" : "prasetyo",
  "password" : "rahasia",
  "name" : "Chandra Prasetyo"
}
```


Response Body (Success):
```json
{
  "data" : "OK"
}
```

Response Body (Failed):
```json
{
  "errors" : "Username must not blank, ??"
}
```


## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "prasetyo",
  "password" : "rahasia"
}
```


Response Body (Success):
```json
{
  "data" : {
    "token" : "TOKEN",
    "expiredAt" : 234234234234 //miliseconds
  }
}
```

Response Body (Failed, 401):
```json
{
  "errors" : "Username or Password is wrong"
}
```

## Get User

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):

```json
{
  "data": {
    "username": "prasetyo",
    "name": "Chandra Prasetyo"
  }
}
```

Response Body (Failed, 401):
```json
{
  "errors" : "Unauthorized"
}
```

## Update User
Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name" : "Prasetyo Chandra", // put if only want to update name
  "password" : "rahasia" //put if only want to update password
}
```


Response Body (Success):

```json
{
  "data" : {
    "username" : "prasetyo",
    "name" : "Chandra Prasetyo"
  }
}
```

Response Body (Failed):

```json
{
  "errors" : "Username must not blank, ??"
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success):
```json
{
  "data" : "OK"
}
```