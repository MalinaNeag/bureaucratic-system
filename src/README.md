
# API Endpoints

## Base URL
`http://localhost:4567`

### POST /api/loan-request
Submits a loan request by adding a citizen to the queue for the requested book.

**Example request:**
```
POST http://localhost:4567/api/loan-request
Content-Type: application/json

{
    "citizenId": "c123",
    "bookTitle": "Effective Java",
    "bookAuthor": "Joshua Bloch"
}
```

### POST /api/pause-counter
Pauses a counter at a specific department, simulating a coffee break.

**Example request:**
```
POST http://localhost:4567/api/pause-counter
Content-Type: application/json

{
    "department": "BookLoaningDepartment",
    "counterId": "1"
}
```

### POST /api/resume-counter
Resumes a paused counter at a specific department.

**Example request:**
```
POST http://localhost:4567/api/resume-counter
Content-Type: application/json

{
    "department": "BookLoaningDepartment",
    "counterId": "1"
}
```

### POST /api/enroll
Enrolls a new citizen and issues a membership if not already enrolled.

**Example request:**
```
POST http://localhost:4567/api/enroll
Content-Type: application/json

{
    "name": "Jane Doe",
    "id": "u456"
}
```

### POST /api/config
Accepts and loads configuration settings.

**Example request:**
```
POST http://localhost:4567/api/config
Content-Type: application/json

{
    "offices": [
        {
            "name": "Enrollment Office",
            "counters": 2,
            "documents": [
                {
                    "name": "Membership ID",
                    "dependencies": []
                }
            ]
        }
    ]
}
```
