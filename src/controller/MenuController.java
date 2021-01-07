package controller;

import model.*;
import view.*;

import java.sql.*;

public class MenuController {

    Connection conn;
    MainView view;
    UserManager model;

    public MenuController(Connection conn){

        this.conn = conn;
        view = new MainView();
        model = new UserManager(this, conn);
        showLoginMenu();
    }

    public void showLoginMenu() {

        boolean finished = false;
        while (!finished) {
            int choice = view.loginMenu();
            switch (choice){
                case 1:
                    checkAdminId(view.getEmployeeId());
                    break;
                case 2:
                    checkDoctorId(view.getEmployeeId());
                    break;
                case 3:
                    patientLogin(view.registerOrLoginMenu());
                    break;
                default:
                    finished = true;
            }
        }
    }
    //login commands
    private void checkAdminId(int adminId) {

        if (model.doesAdminExist(adminId)){
            view.printString("Logged in as admin #" + adminId);
            showAdminMenu();
        }
        else{
            loginFailed();
        }
    }
    private void checkDoctorId(int employeeId) {
        if (model.doesDoctorExist(employeeId)){
            showDoctorMenu();
        }
        else{
            loginFailed();
        }
    }
    public void patientLogin(int choice){


        if(choice == 1) {
            if(model.doesPatientExist(view.getMedicalId())){
                showPatientMenu();
            }
            else{
                loginFailed();
                showLoginMenu();
            }
            }
        else if(choice == 2){
            model.addPatient(view.getNewPatientDetails());
            showPatientMenu();
        }
        else{
            showLoginMenu();
        }
    }
    private void loginFailed() {

        view.loginFailed();
        showLoginMenu();
    }

    public void showAdminMenu() {

        boolean finished = false;
        while (!finished) {
        int choice = view.adminMenu();
            switch (choice){
                case 1:
                    addDoctor(view.getNewDoctorDetails());
                    break;
                case 2:
                    removeDoctor(view.getEmployeeId());
                    break;
                case 3:
                    addSpecial(view.getSpecial(), view.getCost());
                    break;
                case 4:
                    view.printString(model.getPatientList());
                    break;
                case 5:
                    view.printString(model.getMedicalRecord(view.getMedicalId()));
                    break;
                case 6:
                    view.printString(model.getCostList());
                    break;
                default:
                    finished = true;
            }
        }
    }
    //admin commands
    private void addDoctor(String[] newDoctorDetails) {

        if(model.addDoctor(newDoctorDetails)){
            view.printString(String.format("%s added!", model.getSelectedDoctorSimple()));
        }
        else{
            view.printString("Failed to add Doctor, please check information is correct");
        }
    }
    private void removeDoctor(int employeeId) {

        if(model.removeDoctor(employeeId)){
            view.printString(String.format("Doctor with employeeId #%d removed!", employeeId));
        }
        else{
            view.printString("Failed to delete Doctor, doctor doesn't Exist");
        }
    }
    private void addSpecial(String special, double cost) {

        if (model.addSpecial(special, cost)){
            view.printString(String.format("%s added as a new specialization", special));
        }
        else{
            view.printString("Failed to add Specialization, already exists");
        }
    }

    public void showDoctorMenu() {

        boolean finished = false;
        while (!finished) {
            view.printString(model.getSelectedDoctorSimple());
            int choice = view.doctorMenu();
            switch (choice){
                case 1:
                    viewMedicalRecord();
                    break;
                case 2:
                    createMedicalRecord();
                    break;
                case 3:
                    viewAppointments();
                    break;
                case 4:
                    changeAvailability();
                    break;
                default:
                    finished = true;
            }
        }
    }
    //doctor commands
    private void viewMedicalRecord() {

        int medicalId = view.getMedicalId();
        view.printString(model.getMedicalRecord(medicalId));
    }
    private void createMedicalRecord() {

        int medicalId = view.getMedicalId();
        String diagnosis = view.getDiagnosis();
        String description = view.getDescription();
        if(model.createMedicalRecord(medicalId, diagnosis, description)){
            view.printString("Record successfully created");
        }
        else{
            view.printString("Failed to create record");
        }
    }
    private void viewAppointments() {

        view.printString(model.getAppointments());
    }
    private void changeAvailability() {

        String date = view.getAppointmentDate();
        String time = view.getAppointmentTime();
        int availablility = view.getAvailability();
        if(model.changeAvailability(date, time, availablility)){
            view.printString("Availability changed");
        }
        else{
            view.printString("Failed to change availability");
        }
    }

    public void showPatientMenu() {

        boolean finished = false;
        while (!finished) {
            view.printString(model.getLoggedInPatientSimple());
            view.printString(model.getSelectedDoctorSimple());
            int choice = view.patientMenu();
            switch (choice) {
                case 1:
                    view.printString(model.showPatientDetails());
                    break;
                case 2:
                    showUpdatePatientMenu();
                    break;
                case 3:
                    view.printString(model.getDoctors());
                    break;
                case 4:
                    view.printString(model.searchDoctorsWithSpecial(view.getSpecial()));
                    break;
                case 5:
                    if(!model.setSelectedDoctor(view.getEmployeeId())){
                        view.printString("Could not find doctor, please double check ID number");
                    }
                    break;
                case 6:
                    view.printString(model.getSelectedDoctorSimple()+", is available the following times next week:");
                    view.printString(model.getAvailableTimes());
                    break;
                case 7:
                    checkAppointment();
                    break;
                default:
                    finished = true;
            }
        }
    }
    //patient commands
    private void checkAppointment() {

        String date = view.getAppointmentDate();
        String time = view.getAppointmentTime();
        String doctor = model.getSelectedDoctorSimple();

        if(model.bookAppointment(date, time)){
            view.printString(String.format("Appointment booked! %s at %s with %s", date, time, doctor));
        }
        else{
            view.printString("Booking failed. Please try again");
        }

    }
    private void showUpdatePatientMenu() {

        boolean finished = false;
        while (!finished) {
            view.printString(model.getLoggedInPatientSimple());
            int choice = view.updatePatientMenu();
            switch (choice) {
                case 1:
                    model.getLoggedInPatient().setfName(view.getNewAttribute("First Name", model.getLoggedInPatient().getfName()));
                    break;
                case 2:
                    model.getLoggedInPatient().setlName(view.getNewAttribute("Last Name", model.getLoggedInPatient().getlName()));
                    break;
                case 3:
                    model.getLoggedInPatient().setGender(view.getNewAttribute("Gender", model.getLoggedInPatient().getGender()));
                    break;
                case 4:
                    model.getLoggedInPatient().setAddress(view.getNewAttribute("Address", model.getLoggedInPatient().getAddress()));
                    break;
                case 5:
                    model.getLoggedInPatient().setPhoneNumber(view.getNewAttribute("Phone Number", model.getLoggedInPatient().getPhoneNumber()));
                    break;
                case 6:
                    model.getLoggedInPatient().setBirthDate(view.getNewAttribute("BirthDate", model.getLoggedInPatient().getBirthDate()));
                    break;
                default:
                    model.updatePatient();
                    finished = true;
            }
        }
    }
}
