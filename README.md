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

### Backend Setup

1. Navigate to the backend directory:
   ```
   cd backend
   ```

2. Build the application:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the frontend directory:
   ```
   cd frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Run the development server:
   ```
   npm run dev
   ```

### Kafka Setup

1. Download and extract Apache Kafka
2. Start the Zookeeper service:
   ```
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

3. Start the Kafka broker service:
   ```
   bin/kafka-server-start.sh config/server.properties
   ```

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
