# CropAdvisor — Crop Disease Query and Advisory Ticketing

A Spring Boot + MySQL + HTML/CSS/JavaScript project based on the CropAdvisor requirements.

## Implemented requirements

1. Farmer raises a ticket with crop, symptoms and optional photo reference.
2. Officer is automatically assigned from the farmer's region.
3. Assigned officer enters a recommendation and closes the ticket.
4. Farmer can reopen a closed ticket if the issue persists.
5. Tickets pending for more than 48 hours are marked as escalated.
6. Only the assigned officer can close a ticket.
7. Reopening resets the 48-hour resolution timer.
8. Business rules are enforced in the service layer.
9. Validation and a global exception handler are included.
10. Frontend is included inside the Spring Boot project.
11. No Postman is required. All operations can be performed from the browser UI.

## Technology

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL
- HTML
- CSS
- JavaScript
- Lombok

## Database setup

Run this in MySQL Workbench:

```sql
CREATE DATABASE cropadvisor;
```

Then open:

`src/main/resources/application.properties`

and set:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

Use your local MySQL password.

## Run in IntelliJ IDEA

1. Open the `CropAdvisor` folder in IntelliJ IDEA.
2. Make sure Java 17 or a compatible newer JDK is configured.
3. Make sure MySQL Server is running.
4. Create the `cropadvisor` database.
5. Open `CropAdvisorApplication.java`.
6. Click the green Run button.
7. Open:

http://localhost:8080

No Postman is required.

## Recommended first-time data entry

From the browser:

1. Add a Region.
2. Add an active Officer for that Region.
3. Add a Farmer in the same Region.
4. Raise a Ticket for that Farmer.
5. The system automatically assigns the active Officer from that Region.
6. Use `Close & Recommend` to enter the recommendation and close the ticket.
7. Use `Reopen` to reopen the ticket as the ticket-owning farmer.
8. Reopening starts a fresh 48-hour timer.

## Escalation

The backend checks the 48-hour timer and marks overdue open tickets as escalated.

The dashboard also displays the current number of tickets pending beyond 48 hours.

## Main endpoints

- `POST /api/regions`
- `GET /api/regions`
- `POST /api/farmers`
- `GET /api/farmers`
- `POST /api/officers`
- `GET /api/officers`
- `POST /api/tickets`
- `GET /api/tickets`
- `GET /api/tickets/escalated`
- `GET /api/tickets/{id}`
- `PUT /api/tickets/{id}/close`
- `PUT /api/tickets/{id}/reopen?farmerId={id}`
