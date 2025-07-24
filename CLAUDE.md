# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java Spring Boot calendar application built with Gradle. The project uses Spring Boot 3.5.3 with Java 17 and follows standard Spring Boot conventions.

## Common Commands

### Building and Running
- `./gradlew build` - Build the entire project
- `./gradlew bootRun` - Run the Spring Boot application
- `./gradlew clean` - Clean build artifacts

### Testing
- `./gradlew test` - Run all tests
- `./gradlew test --tests "ClassName"` - Run specific test class
- `./gradlew test --tests "ClassName.methodName"` - Run specific test method

### Development
- `./gradlew bootJar` - Create executable JAR file
- `./gradlew dependencies` - View project dependencies

## Architecture

This is a minimal Spring Boot web application with:

- **Main Application**: `CalenderApplication.java` - Standard Spring Boot entry point with `@SpringBootApplication`
- **Package Structure**: `com.example.calender` - Base package for all application code
- **Build System**: Gradle with Spring Boot plugin for dependency management
- **Testing**: JUnit 5 with Spring Boot Test integration
- **Web Framework**: Spring Web MVC (included via spring-boot-starter-web)

The application is currently a skeleton with minimal functionality - just the basic Spring Boot setup with a context loading test.

## Key Dependencies

- Spring Boot Starter Web - For REST API and web functionality
- Spring Boot Starter Test - For testing with JUnit 5
- Java 17 toolchain

## Configuration

- Application properties in `src/main/resources/application.properties`
- Default application name: "calender"
- Uses standard Spring Boot auto-configuration