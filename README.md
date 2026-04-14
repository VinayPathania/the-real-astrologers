# The Real Astrologers 🌌

A modern, full-stack web application that generates highly personalized astrological readings. By combining precise astronomical mathematics with cutting-edge Generative AI, this platform provides users with detailed insights into their career, health, relationships, and future outlook based on their exact time and location of birth.

## ✨ Features

* **AI-Powered Insights:** Translates complex mathematical chart data into empathetic, human-readable paragraphs using the Google Gemini AI API.
* **Precise Geocoding:** Automatically converts user-entered city names (e.g., "Kangra") into exact latitude and longitude coordinates using OpenStreetMap.
* **Secure Authentication:** Features a robust, stateless login and registration system utilizing Spring Security, BCrypt password hashing, and JSON Web Tokens (JWT).
* **Personalized Dashboard:** A customized "My History" tab allows logged-in users to securely view and manage all previously generated charts.
* **Responsive UI:** A clean, form-driven frontend built with React and Vite for a seamless user experience.

## 🛠️ Technology Stack

**Frontend:**
* React.js
* Vite (for rapid development and building)
* Standard CSS3 (Custom responsive styling)

**Backend:**
* Java Spring Boot (REST API framework)
* Spring Security & JWT (Stateless authentication)
* Hibernate / JPA (Object-Relational Mapping)

**Database & APIs:**
* PostgreSQL (Relational database with One-to-Many user mapping)
* Google Gemini API (Natural Language Generation)
* OpenStreetMap API (Coordinate Geocoding)

## 🚀 Getting Started

### Prerequisites
* Node.js (v20 or v22+)
* Java Development Kit (JDK 17+)
* PostgreSQL installed and running on port 5432
* A free [Google Gemini API Key](https://aistudio.google.com/)

### Backend Setup (Spring Boot)
1. Open the backend Java folder in your IDE (e.g., IntelliJ IDEA).
2. Open `src/main/resources/application.properties` and update your database credentials and API key:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
   spring.datasource.username=your_postgres_username
   spring.datasource.password=your_postgres_password
   spring.jpa.hibernate.ddl-auto=update

   # Gemini API
   gemini.api.key=YOUR_GEMINI_API_KEY
