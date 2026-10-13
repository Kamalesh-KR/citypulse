# CityPulse: AI-Driven Urban Traffic Intelligence Platform

A web-based urban traffic intelligence platform that monitors live traffic, predicts congestion, and recommends traffic-aware routes.

## 🚧 Status

In active development (~85% complete).

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL 8
- HTML5, CSS3, JavaScript
- Leaflet.js
- GraphHopper (routing)
- Photon Geocoder
- Maven

## ✨ Features

- Live traffic monitoring with map heatmap
- Congestion prediction using historical trends and weather
- Traffic-aware route recommendation with weighted scoring
- Emergency route simulation (Ambulance / Fire Service)
- Smart alerts for congestion and anomalies
- Historical traffic analytics
- Citizen report submission
- CityPulse AI assistant
- World map covering 130+ countries with city markers and timezone clocks

## 🏗️ Architecture

- Layered MVC (Controller → Service → Repository → Entity)
- RESTful APIs with DTO-based responses
- Session-based authentication with BCrypt password hashing
- Honest traffic data handling (AVAILABLE vs UNAVAILABLE)

## 📦 Build and Run

    git clone https://github.com/Kamalesh-KR/citypulse.git
    cd citypulse
    ./mvnw clean install
    ./mvnw spring-boot:run

The application will be available at http://localhost:8080/

## 📁 Project Structure

    src/main/java/com/citypulse/
    ├── controller/     REST endpoints
    ├── service/        Business logic
    ├── repository/     Data access
    ├── model/          JPA entities
    ├── dto/            Request/Response DTOs
    ├── routing/        GraphHopper and Photon integration
    ├── security/       Spring Security config
    └── exception/      Global exception handling

## 📌 Notes

- Large runtime assets (geocoder/, routing-engine/graph-cache/, *.osm.pbf, *.jar) are excluded from Git and must be downloaded separately.
- The project intentionally does not fabricate traffic values. Routes without matched traffic records are labelled UNAVAILABLE.

## 👤 Author

Kamalesh - https://github.com/Kamalesh-KR
