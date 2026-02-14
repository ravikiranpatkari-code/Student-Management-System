public class Main {
    public static void main(String[] args) {
        StudentDatabase database = new StudentDatabase("data/students.db");
        StudentManager manager = new StudentManager(database);
        manager.start();
    }
}
