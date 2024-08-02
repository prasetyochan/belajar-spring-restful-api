# Address API Spec
## Create Address

Endpoint : POST /api/contacts/{idContact}/addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street": "Jl. Street",
  "city" : "City A",
  "province" : "Provinsi A",
  "country" : "Country A",
  "postalCode" : "12345"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomString",
    "street" : "Jl. Street",
    "city" : "City A",
    "province" : "Provinsi A",
    "country" : "Country A",
    "postalCode" : "12345"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact is not found"
}
```

## Update Address

Endpoint : PUT /api/contacts/{idContact}/addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street": "Jl. Street",
  "city" : "City A",
  "province" : "Provinsi A",
  "country" : "Country A",
  "postalCode" : "12345"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomString",
    "street" : "Jl. Street",
    "city" : "City A",
    "province" : "Provinsi A",
    "country" : "Country A",
    "postalCode" : "12345"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## Get Address


Endpoint : GET /api/contacts/{idContact}/Addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "id" : "randomString",
    "street" : "Jl. Street",
    "city" : "City A",
    "province" : "Provinsi A",
    "country" : "Country A",
    "postalCode" : "12345"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```


## Remove Address

Endpoint : DELETE /api/contacts/{idContact}/Addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## List Address

Endpoint : GET /api/contacts/{idContact}/addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": [
    {
      "id": "randomString",
      "street": "Jl. Street",
      "city": "City A",
      "province": "Provinsi A",
      "country": "Country A",
      "postalCode": "12345"
    }
  ]
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact is not found"
}
```
