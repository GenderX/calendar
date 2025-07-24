# Duty Calendar

This is a simple duty calendar application built with Spring Boot and Thymeleaf.

## Features

*   Displays a monthly calendar with duty assignments.
*   Allows navigation between months.
*   Fetches duty schedules and comments from a backend API.
*   Displays duty statistics for the current year.
*   Includes a lottery feature to randomly select a winner from a list of participants.
*   Shows weather information for the user's current location.

## Tech Stack

*   **Backend:** Spring Boot, Java 17
*   **Frontend:** Thymeleaf, HTML, CSS, JavaScript
*   **Build Tool:** Gradle

## How to Run

1.  **Prerequisites:**
    *   Java 17 or higher
    *   Gradle

2.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/calendar.git
    ```

3.  **Navigate to the project directory:**
    ```bash
    cd calendar
    ```

4.  **Run the application:**
    ```bash
    ./gradlew bootRun
    ```

5.  **Open your browser and go to:**
    ```
    http://localhost:8080
    ```

## API Endpoints

*   `GET /api/schedule?year={year}&month={month}`: Fetches the duty schedule for the given year and month.
*   `GET /api/statistics?year={year}`: Fetches the duty statistics for the given year.
*   `POST /api/comments`: Saves a comment for a specific date.

## Project Structure

*   `src/main/java`: Contains the Spring Boot application code.
*   `src/main/resources/static`: Contains static assets like CSS, JavaScript, and images.
*   `src/main/resources/templates`: Contains the Thymeleaf templates.
*   `build.gradle`: The Gradle build file.