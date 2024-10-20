# Bureaucracy Manager - CEBP PROJECT

## Overview
The Bureaucracy Manager is a software system designed to guide and assist clients navigating the often complex procedures of a classic bureaucratic system. The application simulates a public library environment where citizens request various documents or services. The system can manage an arbitrary number of offices and documents, where obtaining certain documents requires prior completion of intermediate tasks.

## Key Features
- **Office Management**: The application supports multiple offices with specific document-related functions.
- **Document Dependencies**: Certain documents may require prerequisites to be obtained.
- **Queue Management**: Simulated counters handle user requests, with possible temporary closures for breaks.
- **Concurrency Handling**: The system effectively manages multiple threads simulating customer interactions.

## Configuration Elements
The system operates based on a configuration file containing:
- Offices and the documents they issue.
- Dependencies between documents indicating which documents are needed to receive another.

## Departments
<img width="248" alt="image" src="https://github.com/user-attachments/assets/18d527d9-995a-4750-8520-77bcd2a04fa4">

### 1. Enrollment Department
**Functions:**
- Issue Citizen Membership Cards.
- Issue Admin Membership Cards.
- Verify Identity Cards (e.g., passport, birth certificate).
- Manage member information (creation, deletion, and updates).

**Office Counters:** 1 office

### 2. Book Loaning Department
**Functions:**
- Borrow and return books.
- Issue Book Loan Slips for valid transactions.
- Process Return Confirmation Slips upon book returns.
- Monitor late returns and enforce penalties (issue Late Return Penalty Notices).
- Block further loans if the user has:
  - Books past the due date.
  - More than 3 past-due books (until fees are paid).

**Office Counters:** 3 offices (may be temporarily closed due to "coffee breaks" or system overload).

### 3. Library Administration Department
**Functions:**
- Maintain a Library Book Availability Report for all users.
- Administer the Loan History Report (including checking for past-due books and penalties).
- Process fees for Late Return Penalty Notices.
- Manage the library's database (inventory, overdue books, member management).
- Admins have additional privileges (viewing loan histories, bypassing some restrictions).

**Office Counters:** 2 offices

## Concurrency Issues and Solutions

### 1. Admin Updating Books Database
**Problem:** Concurrent requests from citizens or other admins may cause data inconsistencies during updates.

**Solution:**
- Implement a write lock on the database during updates to prevent access from other operations.
- Notify users when the database is being updated.
<img width="277" alt="image" src="https://github.com/user-attachments/assets/167f853c-c8b1-45b7-9408-d43f18789cce">

### 2. Waiting for a Free Counter
**Problem:** Multiple users trying to access a counter may lead to frustration if they have to wait.

**Solution:**
- Use a queue management system for each counter.
- Provide estimated wait times and notifications when it’s their turn.
<img width="337" alt="image" src="https://github.com/user-attachments/assets/a7d60122-958e-4eb1-97cc-9dab48bea134">

### 3. Borrowing a Book
**Process:**
1. Citizen requests to borrow a book.
2. Lock the book for processing.
3. Conduct synchronized checks:
   - Identity Verification
   - Past-Due Book Check
   - Outstanding Fees Check
4. If all checks pass, issue the loan slip and update the book status.
5. Release locks for further processing.
<img width="452" alt="image" src="https://github.com/user-attachments/assets/318aa4e2-6290-4a0c-830e-d1446563b8a9">

## Dependency Flow with Concurrency Handling

### 1. Enrollment Process for Citizens
1. Citizen presents their Identity Card.
2. The system verifies the identity (synchronized check).
3. Upon successful verification, the citizen is issued a Citizen Membership Card.

### 2. Enrollment Process for Admins
1. Admin presents their Identity Card.
2. The system verifies the identity (synchronized check).
3. Upon successful verification, the admin is issued an Admin Membership Card.

### 3. Borrowing a Book with Document Dependencies
1. Citizen requests to borrow a book.
2. Lock the book for processing.
3. Conduct synchronized checks:
   - Identity Verification
   - Past-Due Book Check
   - Outstanding Fees Check
4. If all checks pass, issue the loan slip and update the book status.
5. Release locks for further processing.

### 4. Returning a Book
1. Citizen presents the Book Loan Slip and Membership Card.
2. The system checks if the return is on time:
   - If late, issue a Late Return Penalty Notice.
3. Generate a Return Confirmation Slip.

### 5. Admin Updating the Books Database
1. Admin requests to update the database.
2. The system places a write lock on the database.
3. The admin makes necessary updates.
4. Once updates are complete, release the lock.

### 6. Accessing Counter Services
1. Citizens or admins arrive at the counter.
2. If busy, users are placed in a queue and informed of their estimated wait time.
3. When a counter is free, the next user in the queue is notified and served.

## Conclusion
The Bureaucracy Manager project aims to provide a robust solution for managing document requests in a public library environment, ensuring efficient handling of customer interactions and administrative tasks through effective concurrency management.
