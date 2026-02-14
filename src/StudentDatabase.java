import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentDatabase {
    private final Path storageFile;
    private final Map<String, Student> studentsByRoll;

    public StudentDatabase(String filePath) {
        this.storageFile = Path.of(filePath);
        this.studentsByRoll = new LinkedHashMap<>();
        load();
    }

    public boolean addStudent(Student student) {
        if (studentsByRoll.containsKey(student.getRollNumber())) {
            return false;
        }
        studentsByRoll.put(student.getRollNumber(), student);
        save();
        return true;
    }

    public Optional<Student> getStudentByRoll(String rollNumber) {
        return Optional.ofNullable(studentsByRoll.get(rollNumber));
    }

    public List<Student> searchByName(String nameQuery) {
        String normalized = nameQuery.toLowerCase(Locale.ROOT);
        return studentsByRoll.values()
            .stream()
            .filter(student -> student.getName().toLowerCase(Locale.ROOT).contains(normalized))
            .sorted(Comparator.comparing(Student::getName))
            .collect(Collectors.toList());
    }

    public List<Student> searchByCourse(String courseQuery) {
        String normalized = courseQuery.toLowerCase(Locale.ROOT);
        return studentsByRoll.values()
            .stream()
            .filter(student -> student.getCourse().toLowerCase(Locale.ROOT).contains(normalized))
            .sorted(Comparator.comparing(Student::getRollNumber))
            .collect(Collectors.toList());
    }

    public Collection<Student> getAllStudents() {
        return studentsByRoll.values()
            .stream()
            .sorted(Comparator.comparing(Student::getRollNumber))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean updateStudent(Student updatedStudent) {
        if (!studentsByRoll.containsKey(updatedStudent.getRollNumber())) {
            return false;
        }
        studentsByRoll.put(updatedStudent.getRollNumber(), updatedStudent);
        save();
        return true;
    }

    public boolean deleteStudent(String rollNumber) {
        Student removed = studentsByRoll.remove(rollNumber);
        if (removed == null) {
            return false;
        }
        save();
        return true;
    }

    public void save() {
        try {
            if (storageFile.getParent() != null) {
                Files.createDirectories(storageFile.getParent());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storageFile.toFile()))) {
                oos.writeObject(new ArrayList<>(studentsByRoll.values()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save student records", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(storageFile)) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storageFile.toFile()))) {
            List<Student> students = (List<Student>) ois.readObject();
            studentsByRoll.clear();
            for (Student student : students) {
                studentsByRoll.put(student.getRollNumber(), student);
            }
        } catch (EOFException e) {
            studentsByRoll.clear();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load student records", e);
        }
    }
}
