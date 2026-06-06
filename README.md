# ShareIt

Item-sharing and rental platform built with Java, Spring Boot, PostgreSQL and Docker.

The system allows users to share, search, and book items. Item owners can approve or reject booking requests, and users can leave feedback after successful rentals.

---

## Architecture

The system consists of two modules:

### Gateway Service
Responsible for request validation and filtering. Only valid requests are forwarded to the core service via HTTP.

### Core Service
Handles all business logic and data persistence.

The system is organized into the following domains:

- **Users** — user management
- **Items** — items available for sharing
- **Bookings** — item booking and rental management
- **Requests** — requests for unavailable items
- **Comments** — feedback on items after successful booking

---

## Tech Stack

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- REST API
- JUnit
- Mockito
- Maven
- Docker

---

## Features

### Items
- Create and manage items
- Search items by name and description
- Control item availability

### Bookings
- Request item bookings
- Approve or reject bookings
- View booking history (past, current, future states)

### Requests
- Create requests for unavailable items
- Track item demand

### Comments
- Add comments after at least one successful booking
- View item feedback

---

## Testing

The project includes comprehensive unit and integration tests using JUnit and Mockito.

Tests cover:

- Business logic validation
- Service layer behavior
- Edge cases and error handling
- Booking state transitions and filtering logic

---

## Key Logic Example

The booking service implements complex filtering logic for different booking states and user roles (owner vs booker), including:

- ALL
- PAST
- CURRENT
- FUTURE
- WAITING
- REJECTED

This ensures correct data visibility depending on user context.

---

## My Contribution

Worked on backend implementation of the ShareIt system as part of a training project.

My responsibilities included:

- Implemented booking service with complex filtering logic for different booking states
- Developed item booking workflow (request → approval → completion)
- Built REST API endpoints for items, bookings, requests, and comments
- Worked on repository-level query logic for user-based data access
- Participated in designing layered architecture (gateway + core service separation)

---

## Notes

- Implements gateway + core service architecture
- Strong focus on unit and integration testing
- Demonstrates complex business logic handling in booking workflows
- Built as a layered backend system with clear separation of responsibilities
