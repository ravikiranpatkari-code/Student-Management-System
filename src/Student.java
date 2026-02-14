import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String rollNumber;
    private String name;
    private String course;
    private LocalDate dateOfBirth;
    private final Map<String, Double> grades;
    private final Map<LocalDate, Boolean> attendance;

    public Student(String rollNumber, String name, String course, LocalDate dateOfBirth) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.course = course;
        this.dateOfBirth = dateOfBirth;
        this.grades = new LinkedHashMap<>();
        this.attendance = new LinkedHashMap<>();
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Map<String, Double> getGrades() {
        return Collections.unmodifiableMap(grades);
    }

    public Map<LocalDate, Boolean> getAttendance() {
        return Collections.unmodifiableMap(attendance);
    }

    public void updateBasicInfo(String name, String course, LocalDate dateOfBirth) {
        this.name = name;
        this.course = course;
        this.dateOfBirth = dateOfBirth;
    }

    public void addOrUpdateGrade(String subject, double score) {
        grades.put(subject, score);
    }

    public void recordAttendance(LocalDate date, boolean present) {
        attendance.put(date, present);
    }

    public double calculateAverageGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (double score : grades.values()) {
            sum += score;
        }
        return sum / grades.size();
    }

    public double calculateAttendancePercentage() {
        if (attendance.isEmpty()) {
            return 0.0;
        }

        long presentCount = attendance.values().stream().filter(Boolean::booleanValue).count();
        return (presentCount * 100.0) / attendance.size();
    }

    @Override
    public String toString() {
        return String.format(
            "Roll: %s | Name: %s | Course: %s | DOB: %s | Avg Grade: %.2f | Attendance: %.2f%%",
            rollNumber,
            name,
            course,
            dateOfBirth,
            calculateAverageGrade(),
            calculateAttendancePercentage()
        );
    }
}
