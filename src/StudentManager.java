import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class StudentManager {
    private final StudentDatabase database;
    private final Scanner scanner;

    public StudentManager(StudentDatabase database) {
        this.database = database;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Student Management System ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> registerStudent();
                case 2 -> viewAllStudents();
                case 3 -> searchStudent();
                case 4 -> editStudent();
                case 5 -> deleteStudent();
                case 6 -> addOrUpdateGrade();
                case 7 -> recordAttendance();
                case 8 -> generateReport();
                case 9 -> running = false;
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        System.out.println("Exiting Student Management System. Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n1. Register Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Edit Student Details");
        System.out.println("5. Delete Student");
        System.out.println("6. Add/Update Grades");
        System.out.println("7. Record Attendance");
        System.out.println("8. Generate Student Report");
        System.out.println("9. Exit");
    }

    private void registerStudent() {
        System.out.println("\n--- Register Student ---");
        String roll = readNonEmpty("Roll Number: ");
        String name = readNonEmpty("Name: ");
        String course = readNonEmpty("Course: ");
        LocalDate dob = readDate("Date of Birth (YYYY-MM-DD): ");

        Student student = new Student(roll, name, course, dob);
        if (database.addStudent(student)) {
            System.out.println("Student registered successfully.");
        } else {
            System.out.println("A student with this roll number already exists.");
        }
    }

    private void viewAllStudents() {
        System.out.println("\n--- Student List ---");
        Collection<Student> students = database.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        students.forEach(student -> {
            System.out.println(student);
            printGradesAndAttendance(student);
        });
    }

    private void searchStudent() {
        System.out.println("\nSearch by: 1) Roll Number 2) Name 3) Course");
        int mode = readInt("Select option: ");
        switch (mode) {
            case 1 -> {
                String roll = readNonEmpty("Enter roll number: ");
                Optional<Student> student = database.getStudentByRoll(roll);
                if (student.isPresent()) {
                    System.out.println(student.get());
                    printGradesAndAttendance(student.get());
                } else {
                    System.out.println("Student not found.");
                }
            }
            case 2 -> {
                String name = readNonEmpty("Enter name keyword: ");
                List<Student> results = database.searchByName(name);
                printSearchResults(results);
            }
            case 3 -> {
                String course = readNonEmpty("Enter course keyword: ");
                List<Student> results = database.searchByCourse(course);
                printSearchResults(results);
            }
            default -> System.out.println("Invalid search option.");
        }
    }

    private void editStudent() {
        System.out.println("\n--- Edit Student ---");
        String roll = readNonEmpty("Enter roll number: ");
        Optional<Student> maybeStudent = database.getStudentByRoll(roll);
        if (maybeStudent.isEmpty()) {
            System.out.println("Student not found.");
            return;
        }

        Student student = maybeStudent.get();
        String name = readNonEmpty("Updated Name (current: " + student.getName() + "): ");
        String course = readNonEmpty("Updated Course (current: " + student.getCourse() + "): ");
        LocalDate dob = readDate("Updated DOB (current: " + student.getDateOfBirth() + ", YYYY-MM-DD): ");

        student.updateBasicInfo(name, course, dob);
        database.updateStudent(student);
        System.out.println("Student details updated.");
    }

    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        String roll = readNonEmpty("Enter roll number: ");
        if (database.deleteStudent(roll)) {
            System.out.println("Student deleted.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private void addOrUpdateGrade() {
        System.out.println("\n--- Add/Update Grade ---");
        String roll = readNonEmpty("Enter roll number: ");
        Optional<Student> maybeStudent = database.getStudentByRoll(roll);
        if (maybeStudent.isEmpty()) {
            System.out.println("Student not found.");
            return;
        }

        Student student = maybeStudent.get();
        String subject = readNonEmpty("Subject: ");
        double score = readDouble("Score (0-100): ");

        student.addOrUpdateGrade(subject, score);
        database.updateStudent(student);
        System.out.println("Grade recorded.");
    }

    private void recordAttendance() {
        System.out.println("\n--- Record Attendance ---");
        String roll = readNonEmpty("Enter roll number: ");
        Optional<Student> maybeStudent = database.getStudentByRoll(roll);
        if (maybeStudent.isEmpty()) {
            System.out.println("Student not found.");
            return;
        }

        Student student = maybeStudent.get();
        LocalDate date = readDate("Attendance Date (YYYY-MM-DD): ");
        boolean present = readYesNo("Present? (y/n): ");

        student.recordAttendance(date, present);
        database.updateStudent(student);
        System.out.println("Attendance recorded.");
    }

    private void generateReport() {
        System.out.println("\n--- Report ---");
        String roll = readNonEmpty("Enter roll number (or type ALL): ");

        if (roll.equalsIgnoreCase("ALL")) {
            viewAllStudents();
            return;
        }

        Optional<Student> maybeStudent = database.getStudentByRoll(roll);
        if (maybeStudent.isEmpty()) {
            System.out.println("Student not found.");
            return;
        }

        Student student = maybeStudent.get();
        System.out.println(student);
        printGradesAndAttendance(student);
    }

    private void printGradesAndAttendance(Student student) {
        System.out.println("  Grades: " + (student.getGrades().isEmpty() ? "None" : student.getGrades()));
        System.out.println("  Attendance History: " + (student.getAttendance().isEmpty() ? "None" : student.getAttendance()));
    }

    private void printSearchResults(List<Student> results) {
        if (results.isEmpty()) {
            System.out.println("No matching students found.");
            return;
        }
        results.forEach(student -> {
            System.out.println(student);
            printGradesAndAttendance(student);
        });
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Value cannot be empty.");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0 || value > 100) {
                    System.out.println("Please enter a score between 0 and 100.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please answer y/n.");
        }
    }
}
