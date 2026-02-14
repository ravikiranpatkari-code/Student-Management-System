# Student Management System (Java)

A console-based Student Management System built in Java. It supports:

- Student registration
- View/search/edit/delete student records
- Grades management with average calculation
- Attendance tracking with percentage calculation
- Student reports (individual or all)

## Project Structure

- `src/Student.java` - student entity and business calculations
- `src/StudentDatabase.java` - file-based persistence and CRUD operations
- `src/StudentManager.java` - menu flow and feature logic
- `src/Main.java` - application entry point

## How to Run

```bash
javac -d out src/*.java
java -cp out Main
```

Data is persisted in `data/students.db` using Java serialization.
