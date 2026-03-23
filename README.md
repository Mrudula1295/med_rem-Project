# Smart Medicine Reminder & Health Record System

A full-stack application built to track daily medication, persist offline reminders using Service Workers, and store health records securely.

## Technologies Used
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (ES6+), Progressive Web App (Service Worker)
- **Backend**: Spring Boot 3.x, Spring Data JPA, Java 17
- **Database**: MySQL

## Prerequisites
- Java 17+ installed
- Maven installed
- MySQL Server installed and running natively or via Docker

## Setup & Running the Application

### 1. Database Configuration
By default, the application connects to a MySQL database named `med_reminder_db`.
Ensure your MySQL is running with the credentials:
- Username: `root`
- Password: `root`

*(If your credentials differ, update `backend/src/main/resources/application.properties`)*

### 2. Running the Backend
1. Open a terminal and navigate to the `backend` folder:
   ```bash
   cd "backend"
   ```
2. Run the Spring Boot application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. The server will start on `http://localhost:8080`.

### 3. Running the Frontend
The frontend consists of static HTML/CSS/JS files and a Service Worker wrapper. Due to CORS and Service Worker constraints, you MUST serve the frontend directory via a local web server (opening the HTML file directly in the browser will result in API and Service Worker errors).

Using Python (Recommended if you have Python installed):
1. Open a new terminal and navigate to the `frontend` folder:
   ```bash
   cd "frontend"
   ```
2. Start the Python HTTP server:
   ```bash
   # For Python 3
   python -m http.server 8000
   ```
3. Open your browser and navigate to `http://localhost:8000/login.html`

*(Alternatively, you can use the VS Code "Live Server" extension on the `frontend` folder)*

## Features
- **User Authentication**: Secure Login and Registration.
- **Medicine Tracking**: Upload medicine images, name, and reminder times.
- **Offline Reminder Alerts**: Utilizes a combination of Backend Schedulers to auto-snooze and Frontend Service Workers to locally trigger notifications even when the tab is inactive!
- **Health Records Centralization**: Store Prescription PDFs and images for quick reference from doctors.
