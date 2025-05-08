# Omegle Clone Chat Application

A real-time chat application built with Spring Boot that allows users to connect and chat with random strangers, similar to Omegle.

## 🚀 Features

- Real-time chat functionality using WebSocket
- Random stranger matching system
- Secure communication
- Spring Security integration
- MySQL database support
- RESTful API endpoints

## 🛠️ Technologies Used

- Java 17
- Spring Boot 3.4.3
- Spring WebSocket
- Spring Security
- Spring Data JPA
- MySQL
- Lombok
- Maven

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- JDK 17 or higher
- Maven
- MySQL Server
- IntelliJ IDEA (recommended)

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/omegle-clone.git
cd omegle-clone
```

### 2. Database Setup
1. Create a MySQL database named `omegle_clone`
2. Update the database configuration in `src/main/resources/application.properties` with your MySQL credentials

### 3. Running the Application in IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" and navigate to the project directory
3. Wait for IntelliJ to index the project and download dependencies
4. Locate the main class: `src/main/java/com/example/omegleclone/OmegleCloneApplication.java`
5. Right-click on the file and select "Run 'OmegleCloneApplication'"

Alternatively, you can run the application using Maven:
```bash
mvn spring-boot:run
```

### 4. Accessing the Application
Once the application is running, you can access it at:
```
http://localhost:8080
```

## 📁 Project Structure

```
src/main/java/com/example/omegleclone/
├── socket/           # WebSocket related classes
├── controller/       # REST controllers
├── model/           # Entity classes
├── repository/      # Data access layer
├── service/         # Business logic
└── config/          # Configuration classes
```

## 🔧 Configuration

The main configuration file is located at `src/main/resources/application.properties`. You may need to modify:
- Database connection settings
- Server port
- WebSocket endpoints
- Security configurations

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- All contributors who have helped with the project
