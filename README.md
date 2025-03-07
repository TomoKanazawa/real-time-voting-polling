# Distributed Real-Time Voting and Polling System

A scalable, fault-tolerant, real-time voting and polling system built with Java Spring Boot and Next.js.

## Project Overview

This project implements a distributed voting system that can handle thousands of concurrent votes with minimal latency. It uses Apache Kafka for real-time messaging and implements leader election algorithms to ensure fault tolerance and consistency.

### Key Features

- **Real-time vote processing**: Votes are processed and results are updated in real-time
- **Fault tolerance**: The system can recover from node failures without losing data
- **Scalability**: Designed to handle thousands of concurrent users
- **Leader election**: Uses distributed consensus algorithms for leader election
- **Secure voting**: Ensures vote integrity and prevents duplicate voting

## Architecture

The system consists of the following components:

- **Backend**: Java Spring Boot application that handles vote processing, leader election, and data persistence
- **Frontend**: Next.js application that provides a user interface for creating polls and voting
- **Kafka**: Message broker for real-time vote transmission and processing
- **Database**: Stores poll data, votes, and user information

## Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Apache Kafka
- Maven

### Running the Application

#### Backend Setup

1. Navigate to the project root directory and then to the backend directory:
   ```bash
   cd real-time-voting-polling/backend
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Verify the backend is running:
   - The Spring Boot application should start on port 8080
   - You should see logs indicating "Started BackendApplication in X seconds"
   - You can test the health endpoint by visiting http://localhost:8080/api/health or running:
     ```bash
     curl http://localhost:8080/api/health
     ```
   - The H2 database console is available at http://localhost:8080/api/h2-console

#### Frontend Setup

1. Open a new terminal window and navigate to the frontend directory:
   ```bash
   cd real-time-voting-polling/frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Run the development server:
   ```bash
   npm run dev
   ```

4. Verify the frontend is running:
   - The Next.js application should start on port 3000
   - You should see a message indicating "Local: http://localhost:3000"
   - Open your browser and navigate to http://localhost:3000 to see the application

#### Running Both Services Together

For convenience, you can run both services in a single terminal using:

```bash
# From the project root
cd real-time-voting-polling/backend && mvn spring-boot:run &
cd ../frontend && npm run dev &
```

To stop both services, use:
```bash
pkill -f "spring-boot|next"
```

### Kafka Setup (Optional for Basic Testing)

Note: For basic testing, Kafka is not required as the application can run with in-memory messaging.

1. Download and extract Apache Kafka
2. Start the Zookeeper service:
   ```bash
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

3. Start the Kafka broker service:
   ```bash
   bin/kafka-server-start.sh config/server.properties
   ```

### Troubleshooting

- **Port conflicts**: If either service fails to start due to port conflicts, you can modify the ports:
  - For backend: Edit `backend/src/main/resources/application.properties` and change `server.port`
  - For frontend: Run with a custom port using `npm run dev -- -p 3001`

- **Backend not starting**: Check Java version with `java -version` and ensure it's 17 or higher

- **Frontend not starting**: Check Node.js version with `node -v` and ensure it's 18 or higher

## API Documentation

The backend provides the following RESTful APIs:

- `/api/auth`: Authentication endpoints
- `/api/polls`: Poll management endpoints
- `/api/votes`: Vote submission and retrieval endpoints

## Distributed Algorithms

This project implements the following distributed algorithms:

1. **Leader Election**: Uses the Bully Algorithm for leader election
2. **Consensus**: Implements a simplified version of the Raft consensus algorithm
3. **Gossip Protocol**: For disseminating information across nodes
4. **Distributed Snapshots**: For capturing the state of the distributed system

## Contributors

- Shiva Kumar Rangapuram
- Tomohiro Kanazawa
- Venkat Dugasani

