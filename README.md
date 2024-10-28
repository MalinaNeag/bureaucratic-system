# Bureaucracy Manager - CEBP PROJECT

## Overview
The Bureaucracy Manager is a software system designed to streamline interactions within a bureaucratic system, using a public library scenario where citizens request various documents or services. This simulation includes managing multiple offices, handling document dependencies, and processing requests through queues and concurrency control.

## Key Features
- **Office Management**: Supports multiple offices with specific functions related to document processing.
- **Document Dependencies**: Manages dependencies where certain documents are prerequisites for others.
- **Queue Management**: Utilizes queues at simulated counters to manage user requests efficiently.
- **Concurrency Handling**: Handles multiple user interactions simultaneously, ensuring data consistency and availability.
- **Coffee Break Simulation**: Simulates temporary closures of counters for coffee breaks, adding realism and complexity to queue management.

## Configuration Elements
The system reads from a configuration file to set up:
- Offices and the documents they handle.
- Dependencies that specify prerequisites for obtaining certain documents.

## API Endpoints
- `POST /api/loan-request`: Processes a book loan request and adds it to the queue.
- `POST /api/pause-counter`: Temporarily pauses a counter to simulate coffee breaks or load management.
- `POST /api/resume-counter`: Resumes operations at a paused counter, managing backlogs efficiently.
- `POST /api/enroll`: Handles the enrollment of new citizens based on provided data.

## Departments
![Department Structure](https://github.com/user-attachments/assets/18d527d9-995a-4750-8520-77bcd2a04fa4)

### Enrollment Department
**Counters:** 1
- Handles citizen registration, verifying identities, and managing membership details.

### Book Loaning Department
**Counters:** 2, with the ability to pause for coffee breaks
- Manages book loans, returns, and penalties related to late returns.
- Utilizes real-time queue management for handling book requests, ensuring efficient processing even during peak times.

### Library Administration Department
**Counters:** 2
- Oversees the library's operations including book availability, loan histories, and administrative updates.
- Capable of pausing operations at counters for administrative reviews or breaks.

## Concurrency Issues and Solutions

### 1. Multiple Loan Requests for the Same Book
**Problem:** Simultaneous requests for the same book can result in conflicts and incorrect loan processing.
**Solution:** Utilizes a ConcurrentHashMap to manage locks on individual books, ensuring that only one process can modify the book’s state at a time.

<img width="452" alt="image" src="https://github.com/user-attachments/assets/318aa4e2-6290-4a0c-830e-d1446563b8a9">

### 2. Enrollment of a Citizen
**Problem:** Concurrent enrollment requests could lead to duplicate records.
**Solution:** Uses synchronized methods in the EnrollmentDepartment to ensure atomic operations for checking and updating citizen records.

### 3. Updating the Books Database
**Problem:** Concurrent database updates by admins could lead to data inconsistencies.
**Solution:** Implements a write lock during database updates to prevent other operations from accessing the database simultaneously.

<img width="277" alt="image" src="https://github.com/user-attachments/assets/167f853c-c8b1-45b7-9408-d43f18789cce">

### 4. Queue Management for Loan Requests
**Problem:** Managing a queue in a multi-threaded environment could lead to lost updates or inconsistent states.
**Solution:** Employs a LinkedBlockingQueue to automatically handle concurrent additions and removals safely.

### 5. Pausing and Resuming Counters
**Problem:** Pausing and resuming counters without proper synchronization could lead to deadlocks or indefinite waits.
**Solution:** Uses boolean flags and synchronization on these flags to manage counter states safely, avoiding deadlocks and ensuring counters are paused and resumed correctly.

## System Configuration
The system is configured through a JSON file, which is loaded at runtime to set up office and document information based on predefined schemas.

## Conclusion
Bureaucracy Manager offers a robust solution for simulating a public library's bureaucratic processes, emphasizing efficient and reliable handling of concurrent user interactions and administrative tasks, including realistic simulations of coffee breaks and load management at multiple counters.
