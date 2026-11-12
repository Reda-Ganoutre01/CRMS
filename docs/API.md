# CRMS API Documentation

## Base URL

When deployed to Tomcat:

```text
http://localhost:8080/location-voitures/api
```

The authentication endpoints below are implemented. Other endpoint groups
remain a target REST contract and are intentionally not implemented yet.

## General rules

- Request and response format: `application/json`
- Dates use ISO-8601 format: `YYYY-MM-DD`
- Monetary values are calculated by the server.
- IDs are numeric values.
- Clients can access their own resources.
- Administrators can manage all resources.

## Authentication

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/auth/login` | Authenticate a user and return an access token/session. | Public |
| `POST` | `/auth/register` | Create a client account. | Public |
| `POST` | `/auth/logout` | Invalidate the current session/token. | Authenticated |
| `GET` | `/auth/me` | Return the authenticated user's profile. | Authenticated |
| `PUT` | `/auth/password` | Change the authenticated user's password. | Authenticated |

### Login request

```json
{
  "email": "admin@example.com",
  "motDePasse": "secret"
}
```

### Login response

```json
{
  "type": "Cookie",
  "user": {
    "id": 1,
    "nom": "Admin",
    "prenom": "Principal",
    "email": "admin@example.com",
    "role": "ADMINISTRATEUR"
  }
}
```

The server stores the JWT in an `HttpOnly`, `SameSite=Strict` cookie named
`JWT`; it is not returned in the JSON response. The browser sends the cookie
automatically on subsequent requests.

For non-browser clients that use bearer authentication, authenticated
requests may send:

```http
Authorization: Bearer <token>
```

The server must be started with a `JWT_SECRET` environment variable or JVM
system property containing at least 32 characters. The signing secret must
not be committed to the repository.

Passwords must be stored as BCrypt hashes. Never send or store a plaintext
password. New-user and password-change code should call the authentication
DAO password-hashing helper before persisting `Utilisateur`.

## Clients

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/clients` | List clients. Supports `page`, `size`, and `search`. | Admin |
| `POST` | `/clients` | Create a client. | Admin |
| `GET` | `/clients/{id}` | Return one client. | Admin or owner |
| `PUT` | `/clients/{id}` | Update client information. | Admin or owner |
| `DELETE` | `/clients/{id}` | Delete/deactivate a client. | Admin |
| `GET` | `/clients/{id}/reservations` | Return the client's reservations. | Admin or owner |
| `GET` | `/clients/{id}/contracts` | Return the client's rental contracts. | Admin or owner |

### Create client

```http
POST /api/clients
Content-Type: application/json
```

```json
{
  "nom": "Alaoui",
  "prenom": "Youssef",
  "cin": "EE123456",
  "email": "youssef@example.com",
  "telephone": "0661123456",
  "adresse": "Rabat",
  "numeroPermis": "P123456"
}
```

## Vehicles

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/vehicles` | List vehicles. Supports `status`, `marque`, `modele`, `page`, and `size`. | Public |
| `POST` | `/vehicles` | Add a vehicle. | Admin |
| `GET` | `/vehicles/{id}` | Return one vehicle. | Public |
| `PUT` | `/vehicles/{id}` | Update vehicle information. | Admin |
| `DELETE` | `/vehicles/{id}` | Delete/deactivate a vehicle. | Admin |
| `GET` | `/vehicles/available` | List vehicles with status `DISPONIBLE`. | Public |
| `GET` | `/vehicles/{id}/reservations` | Return reservations for a vehicle. | Admin |
| `GET` | `/vehicles/{id}/contracts` | Return contracts for a vehicle. | Admin |
| `PATCH` | `/vehicles/{id}/status` | Change vehicle status. | Admin |

### Create vehicle

```json
{
  "immatriculation": "12345-A-6",
  "marque": "Dacia",
  "modele": "Logan",
  "annee": 2024,
  "carburant": "ESSENCE",
  "kilometrage": 12000,
  "prixJour": 250,
  "statut": "DISPONIBLE"
}
```

Valid vehicle statuses:

```text
DISPONIBLE
LOUE
EN_MAINTENANCE
```

## Reservations

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/reservations` | List reservations. | Admin or authenticated client |
| `POST` | `/reservations` | Create a reservation. | Authenticated client |
| `GET` | `/reservations/{id}` | Return one reservation. | Admin or owner |
| `PUT` | `/reservations/{id}` | Modify a pending reservation. | Admin or owner |
| `DELETE` | `/reservations/{id}` | Cancel a reservation. | Admin or owner |
| `PATCH` | `/reservations/{id}/status` | Change reservation status. | Admin |
| `POST` | `/reservations/{id}/confirm` | Confirm a pending reservation. | Admin |
| `POST` | `/reservations/{id}/cancel` | Cancel a reservation. | Admin or owner |
| `GET` | `/reservations/availability` | Check vehicle availability for a date range. | Public |

### Create reservation

```json
{
  "vehiculeId": 2,
  "offreId": 1,
  "dateDebut": "2026-12-01",
  "dateFin": "2026-12-05"
}
```

Required validation:

1. `dateDebut` must be before `dateFin`.
2. The vehicle must exist and not be in maintenance.
3. The selected dates must not overlap another active reservation or contract.
4. The client must be taken from the authenticated user, not trusted from the request body.
5. The server calculates `montantTotal` and applies a valid offer.

## Rental contracts

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/contracts` | List rental contracts. | Admin or authenticated client |
| `POST` | `/contracts` | Create a contract directly or from a confirmed reservation. | Admin |
| `GET` | `/contracts/{id}` | Return one contract. | Admin or owner |
| `PUT` | `/contracts/{id}` | Update a contract before completion. | Admin |
| `DELETE` | `/contracts/{id}` | Delete a contract and release the vehicle. | Admin |
| `POST` | `/contracts/{id}/start` | Start a rental and save departure mileage. | Admin |
| `POST` | `/contracts/{id}/finish` | Finish a rental and save return mileage. | Admin |
| `POST` | `/contracts/{id}/cancel` | Cancel an active contract. | Admin |
| `GET` | `/contracts/{id}/invoice` | Return invoice information. | Admin or owner |

### Start contract

```json
{
  "kmDepart": 45200
}
```

### Finish contract

```json
{
  "kmRetour": 45850
}
```

Required validation:

- `kmRetour` cannot be lower than `kmDepart`.
- A completed or cancelled contract cannot be modified.
- Starting a contract changes the vehicle status to `LOUE`.
- Finishing or cancelling an active contract changes the vehicle status to
  `DISPONIBLE`.

## Offers

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/offers` | List offers. | Public |
| `POST` | `/offers` | Create an offer. | Admin |
| `GET` | `/offers/{id}` | Return one offer. | Public |
| `PUT` | `/offers/{id}` | Update an offer. | Admin |
| `DELETE` | `/offers/{id}` | Delete/deactivate an offer. | Admin |
| `GET` | `/offers/active` | Return offers valid today. | Public |
| `GET` | `/offers/{id}/reservations` | Return reservations using an offer. | Admin |

### Create offer

```json
{
  "titre": "Weekend promotion",
  "description": "10% discount for weekend rentals",
  "pourcentageRemise": 10,
  "dateDebut": "2026-12-01",
  "dateFin": "2026-12-31"
}
```

## Dashboard

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/dashboard/statistics` | Return counts and current revenue. | Admin |
| `GET` | `/dashboard/revenue` | Return revenue for a date range. | Admin |
| `GET` | `/dashboard/recent-contracts` | Return the latest contracts. | Admin |
| `GET` | `/dashboard/vehicle-usage` | Return vehicle usage statistics. | Admin |

### Statistics response

```json
{
  "totalVehicles": 12,
  "availableVehicles": 8,
  "totalClients": 35,
  "activeContracts": 4,
  "revenue": 18500.0
}
```

## Error format

All errors should use the same structure:

```json
{
  "timestamp": "2026-11-11T22:00:00Z",
  "status": 409,
  "error": "CONFLICT",
  "message": "Vehicle is not available for the selected dates.",
  "path": "/api/reservations"
}
```

Recommended status codes:

| Status | Meaning |
|---|---|
| `200` | Successful read or update |
| `201` | Resource created |
| `204` | Successful deletion with no response body |
| `400` | Invalid request data |
| `401` | Missing or invalid authentication |
| `403` | Authenticated but not authorized |
| `404` | Resource not found |
| `409` | Business conflict, such as an unavailable vehicle |
| `500` | Unexpected server error |

## Example request

```bash
curl -X GET http://localhost:8080/location-voitures/api/vehicles/available \
  -H "Accept: application/json"
```
