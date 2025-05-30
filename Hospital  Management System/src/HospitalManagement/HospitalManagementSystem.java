package HospitalManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "root";

    public static boolean checkDoctorAvailabilty(int doctor_id, String appointmentDate, Connection connection) {
        String query = "Select count(*) from appointments where doctor_id = ? and appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            e.getStackTrace();
        }
        return false;
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        scanner.nextLine();
        System.out.println("Enter the patient ID");
        int patientID = scanner.nextInt();
        System.out.println("Enter the Doctor ID");
        int doctorID = scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD)");
        scanner.nextLine();
        String appointmentDate = scanner.nextLine();
        if (patient.getPatientById(patientID) && doctor.getDoctorById(doctorID)) {
            if (checkDoctorAvailabilty(doctorID, appointmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id , doctor_id , appointment_date) Values(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientID);
                    preparedStatement.setInt(2, doctorID);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked !");

                    } else {
                        System.out.println("Failed to book Appointment ");

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available at this date");

            }

        } else {
            System.out.println("Either doctor or patient doesnt exist !!!");
        }

    }

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.getStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner scanner = new Scanner(System.in);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. ADD PATIENT");
                System.out.println("2. VIEW PATIENT");
                System.out.println("3. VIEW DOCTOR");
                System.out.println("4. BOOK APPLICATION");
                System.out.println("5. EXIT");
                System.out.println("Enter your choice");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;

                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;

                    case 5:
                        System.out.println("Exitting......");
                        System.out.println();
                        return;

                    default:
                        System.out.println("Enter valid choice");
                        break;
                }

            }
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

}
