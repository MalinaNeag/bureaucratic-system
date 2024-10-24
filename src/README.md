
# API Endpoints

## Base URL
`http://localhost:4567`

### GET /api/books
Returns a list of all books.

**Example request:**
```
GET http://localhost:4567/api/books
```

### POST /api/loan-request
Submits a loan request.

**Example request:**
```
POST http://localhost:4567/api/loan-request
Content-Type: application/json

{
    "bookId": "123",
    "citizenId": "456"
}
```

### POST /api/enroll
Enrolls a new citizen.

**Example request:**
```
POST http://localhost:4567/api/enroll
Content-Type: application/json

{
    "name": "John Doe",
    "age": 30
}
```

