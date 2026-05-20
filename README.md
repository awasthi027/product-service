# Product Service (Java + Spring Boot)

A Spring Boot product microservice with PostgreSQL persistence.

## Features
- Create product
- Get product by id
- Update product
- Delete product
- Search products with pagination
- Unit tests for API/controller and service logic

## Tech Stack
- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- PostgreSQL
- JUnit 5 + Mockito + MockMvc

## API Endpoints
- `POST /api/products`
- `GET /api/products/{id}`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `GET /api/products/search?q=phone&page=0&size=10`

## Request Example (Create/Update)
```json
{
  "name": "iPhone 15",
  "description": "Latest Apple smartphone",
  "price": 999.99,
  "category": "Electronics"
}
```

## Local Run
Set Neon connection string as environment variable and start app.

```bash
export DATABASE_URL="<your_neon_postgresql_connection_string>"

mvn clean test
mvn spring-boot:run
```

The app runs on `http://localhost:8080`.

## Quick API Check
```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{
	"name":"iPhone 15",
	"description":"Latest Apple smartphone",
	"price":999.99,
	"category":"Electronics"
  }'

curl "http://localhost:8080/api/products/search?q=iphone&page=0&size=10"
```

## Railway Deployment
This repo includes:
- `railway.toml`
- `Procfile`
- `system.properties`
- `Dockerfile`

### Steps
1. Push this project to GitHub.
2. In Railway, create a new project from this GitHub repo.
3. Add environment variable:
   - `DATABASE_URL` = your Neon connection string.
4. Deploy.
5. Railway sets `PORT` automatically and the app binds to it.

## Test
```bash
mvn test
```
