/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

import model.Student;
import model.Fingerprint;
import model.CourseGroup;
import model.Inscription;
import DAO.CourseGroupDAO;
import DAO.InscriptionDAO;
import DAO.ParticipationDAO;
import DAO.StudentDAO;
import DAO.AssistanceDAO;
import view.FingerprintForm;

/**
 *
 * @author D2D
 */
public class FingerprintController {
    
    /** "fingerprintForm" var is an instance of view */
    private FingerprintForm fingerprintForm;
    /** "fingerprint" var is an instance of model */
    private Fingerprint fingerprint;
    
    public FingerprintController(FingerprintForm fingerprintForm, Fingerprint fingerprint){
        this.fingerprintForm = fingerprintForm;
        this.fingerprint = fingerprint;
    }
    
    /**
     * Count captures necessaries to create fingerprint template
     */
    public void showFingerprintMissing() {
        this.fingerprintForm.showMessage("Quantity of missing fingerprints: " + this.fingerprint.recruiter.getFeaturesNeeded());
    }
    
    public int getFeaturesNeeded() {
        return this.fingerprint.recruiter.getFeaturesNeeded();
    }
    
    public void setCourseGroups() {
        try {
            this.fingerprintForm.showMessage("WAIT: Loading course groups...");
            CourseGroupDAO dao = new CourseGroupDAO();
            List<CourseGroup> courses = dao.getAll();
            this.fingerprintForm.setCourseGroups(courses);
            this.fingerprintForm.enableBtnAssistance(true);
            this.fingerprintForm.enableBtnParticipations(true);
            this.fingerprintForm.enableBtnSave(true);
            this.fingerprintForm.enableBtnIdentify(true);

            this.fingerprintForm.showMessage("SUCCESSFUL: Course Groups loaded");
        } catch(SQLException e){
            this.fingerprintForm.showMessage("An error ocurred consulting CourseGroups: "+e.getMessage());
        }
        
    }
    
    public List<Student> getStudents(CourseGroup course) {
        try {
            StudentDAO dao = new StudentDAO();
            return dao.getByCourseGroup(course.getId());
        } catch(SQLException e){
            return new ArrayList<>();
        }
    }
    
    public void enableParticipations(){
        // Get Students
        this.fingerprintForm.setTitleText("YOU ARE RECORDING PARTICIPATIONS");
        CourseGroup course = this.fingerprintForm.getCourseSelected();
        this.fingerprintForm.showInfoMessage("Course selected: " + course.toString());
        List<Student> students = this.getStudents(course);
        if (students == null || students.isEmpty()) {
            this.fingerprintForm.showErrorMessage("Students not found");
            return;
        }
        String str = students.size() + " studens loaded";
        this.fingerprintForm.showMessage(str);
        
        // Turn on thread to listen biometric lector
        this.fingerprint.reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // MEJORATE PERFORMANCE: IT IS ON DURING 20 SECS
                        processParticipations(e, students, course);
                    }
                });
            }
        });
        
        this.start();
    }
    
    private void processParticipations(DPFPDataEvent e, List<Student> students, CourseGroup course) {
        fingerprintForm.showMessage("Fingerprint captured");
        /** Process fingerprint sample */
        DPFPSample sample = e.getSample();

        /** Process the fingerprint sample and create a set of features to incription */
        fingerprint.featuresInscription = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        /** Process the fingerprint sample and create a set of features to verification */
        fingerprint.featuresVerification = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        /** Verify the quality of the fingerprint sample and if is good add it to its recruiter */
        if (fingerprint.featuresInscription == null) {
            return;
        }
        
        try {
            fingerprint.recruiter.addFeatures(fingerprint.featuresInscription); 

            /** Draw captured fingerprint */
            Image image = fingerprint.createFingerprintImage(sample);
            fingerprintForm.drawFingerprint(image);

        } catch (DPFPImageQualityException ex) {
            fingerprintForm.showErrorMessage("ERROR: " + ex.getMessage());
        }
        showFingerprintMissing();
        /** Verify if etmplate was created */
        switch (fingerprint.recruiter.getTemplateStatus()) {
            case TEMPLATE_STATUS_READY: // Capture successful
                fingerprint.setTemplate(fingerprint.recruiter.getTemplate());
                InscriptionDAO inscriptionDAO = new InscriptionDAO();
                ParticipationDAO participationDAO = new ParticipationDAO();
                Student student = fingerprint.identifyFingerprint(students);
                if(student == null){
                    fingerprintForm.showErrorMessage("ERROR: Student not found in course selected (" + course.toString() +")");
                    this.clear();
                    return;
                }
                try {
                    Inscription inscription = inscriptionDAO.getOne(course, student);
                    participationDAO.registerParticipation(inscription.getId());
                } catch(SQLException ex){
                    fingerprintForm.showErrorMessage("ERROR: Participation cannot be registered: " + ex.getMessage());
                    this.clear();
                    return;
                }
                String rockOn = new String(Character.toChars(0x2705));
                fingerprintForm.showSuccessfulMessage(rockOn + " Registered participation for " + student.toString());
                this.clear();
                break;

            case TEMPLATE_STATUS_FAILED: // Capture failed
                fingerprintForm.showErrorMessage("Template cannot be created, try again");
                this.stop();
                this.clear();
                this.start();
                break;
        }
    }
    
    public void enableAssistances(){
        // Get Students
        this.fingerprintForm.setTitleText("YOU ARE RECORDING ASSISTANCES");
        CourseGroup course = this.fingerprintForm.getCourseSelected();
        this.fingerprintForm.showInfoMessage("Course selected: " + course.toString());
        List<Student> students = this.getStudents(course);
        if (students == null || students.isEmpty()) {
            this.fingerprintForm.showErrorMessage("Students not found");
            return;
        }
        String str = students.size() + " studens loaded";
        this.fingerprintForm.showMessage(str);
        
        // Turn on thread to listen biometric lector
        this.fingerprint.reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // MEJORATE PERFORMANCE: IT IS ON DURING 20 SECS
                        processAssistances(e, students, course);
                    }
                });
            }
        });
        
        this.start();
    }
    
    private void processAssistances(DPFPDataEvent e, List<Student> students, CourseGroup course) {
        fingerprintForm.showMessage("Fingerprint captured");
        /** Process fingerprint sample */
        DPFPSample sample = e.getSample();

        /** Process the fingerprint sample and create a set of features to incription */
        fingerprint.featuresInscription = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        /** Process the fingerprint sample and create a set of features to verification */
        fingerprint.featuresVerification = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        /** Verify the quality of the fingerprint sample and if is good add it to its recruiter */
        if (fingerprint.featuresInscription == null) {
            return;
        }
        
        try {
            fingerprint.recruiter.addFeatures(fingerprint.featuresInscription); 

            /** Draw captured fingerprint */
            Image image = fingerprint.createFingerprintImage(sample);
            fingerprintForm.drawFingerprint(image);

        } catch (DPFPImageQualityException ex) {
            fingerprintForm.showErrorMessage("ERROR: " + ex.getMessage());
        }
        showFingerprintMissing();
        /** Verify if etmplate was created */
        switch (fingerprint.recruiter.getTemplateStatus()) {
            case TEMPLATE_STATUS_READY: // Capture successful
                fingerprint.setTemplate(fingerprint.recruiter.getTemplate());
                InscriptionDAO inscriptionDAO = new InscriptionDAO();
                AssistanceDAO assistanceDAO = new AssistanceDAO();
                Student student = fingerprint.identifyFingerprint(students);
                if(student == null){
                    fingerprintForm.showErrorMessage("ERROR: Student not found in course selected (" + course.toString() +")");
                    this.clear();
                    return;
                }
                Boolean stateRegistered = false;
                try {
                    Inscription inscription = inscriptionDAO.getOne(course, student);
                    if (assistanceDAO.currentAssistance(inscription.getId())){
                        assistanceDAO.registerAssistance(inscription.getId());
                        stateRegistered = true;
                    }else{
                        fingerprintForm.showInfoMessage("Unable to register assistance a second time");
                    }
                } catch(SQLException ex){
                    fingerprintForm.showErrorMessage("ERROR: Assistance cannot be registered: " + ex.getMessage());
                    this.clear();
                    return;
                }
                
                if (stateRegistered){
                    String rockOn = new String(Character.toChars(0x2705));
                    fingerprintForm.showSuccessfulMessage(rockOn + " Registered assistance for " + student.toString());
                }
                this.clear();
                break;
                
            case TEMPLATE_STATUS_FAILED: // Capture failed
                fingerprintForm.showErrorMessage("Template cannot be created, try again");
                this.stop();
                this.clear();
                this.start();
                break;
        }
    }

    public void enableSave(){
        this.fingerprintForm.setTitleText("YOU ARE TRYING SAVE A FINGERPRINT IN A STUDENT");
        // Turn on thread to listen biometric lector
        this.fingerprint.reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // MEJORATE PERFORMANCE: IT IS ON DURING 20 SECS
                        processSave(e);
                    }
                });
            }
        });
        
        this.start();
    }
    
    private void processSave(DPFPDataEvent e) {
        
        fingerprintForm.showMessage("Fingerprint captured");
        /** Process fingerprint sample */
        DPFPSample sample = e.getSample();

        /** Process the fingerprint sample and create a set of features to incription */
        fingerprint.featuresInscription = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        /** Process the fingerprint sample and create a set of features to verification */
        fingerprint.featuresVerification = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        /** Verify the quality of the fingerprint sample and if is good add it to its recruiter */
        if (fingerprint.featuresInscription == null) {
            return;
        }
        
        try {
            fingerprint.recruiter.addFeatures(fingerprint.featuresInscription); 

            /** Draw captured fingerprint */
            Image image = fingerprint.createFingerprintImage(sample);
            fingerprintForm.drawFingerprint(image);

        } catch (DPFPImageQualityException ex) {
            fingerprintForm.showErrorMessage("ERROR: " + ex.getMessage());
        }
        showFingerprintMissing();
        /** Verify if te    mplate was created */
        switch (fingerprint.recruiter.getTemplateStatus()) {
            case TEMPLATE_STATUS_READY: // Capture successful
                fingerprint.setTemplate(fingerprint.recruiter.getTemplate());
                ByteArrayInputStream datosHuella = new ByteArrayInputStream(this.fingerprint.template.serialize());
                
                String identificationStudent = JOptionPane.showInputDialog("Type identification number:");
                
                Student student = new Student();
                StudentDAO dao = new StudentDAO();
                try{
                    student = dao.getStudentByIdentification(identificationStudent);
                }catch(SQLException ex){
                    this.fingerprintForm.showErrorMessage("Student for " + identificationStudent + " error: " + ex.getMessage());
                }
                //student.setIdentification(identificationStudent);
                student.setFingerprintData(datosHuella);
                student.setFingerprintSize(this.fingerprint.template.serialize().length);
                try {
                    int rowCount = dao.saveFingerprint(student);
                    if(rowCount == 1){
                        this.fingerprintForm.showSuccessfulMessage("Fingerprint saved correctly of " + student.toString());
                    }else{
                        this.fingerprintForm.showErrorMessage("Fingerprint not saved for " + identificationStudent);
                    }
                } catch (SQLException ex) {
                    this.fingerprintForm.showErrorMessage("Fingerprint not saved! "+ex.getMessage());
                }

                this.clear();
                this.fingerprintForm.enableBtnSave(false);
                break;

            case TEMPLATE_STATUS_FAILED: // Capture failed
                fingerprintForm.showErrorMessage("Template cannot be created, try again");
                this.stop();
                this.clear();
                this.start();
                break;
        }
    }

    public void enableIdentify(){
        // Get Students
        this.fingerprintForm.setTitleText("YOU ARE TRYING IDENTIFY A STUDENT");
        CourseGroup course = this.fingerprintForm.getCourseSelected();
        this.fingerprintForm.showInfoMessage("Course selected: " + course.toString());
        List<Student> students = this.getStudents(course);
        if (students == null || students.isEmpty()) {
            this.fingerprintForm.showErrorMessage("Students not found");
            return;
        }
        String str = students.size() + " studens loaded";
        this.fingerprintForm.showMessage(str);

        // Turn on thread to listen biometric lector
        this.fingerprint.reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // MEJORATE PERFORMANCE: IT IS ON DURING 20 SECS
                        processIdentify(e, students, course);
                    }
                });
            }
        });
        
        this.start();
    }
    
    private void processIdentify(DPFPDataEvent e, List<Student> students, CourseGroup course) {
        fingerprintForm.showMessage("Fingerprint captured");
        /** Process fingerprint capture */
        DPFPSample sample = e.getSample();

        /* Process fingerprint sample and create a set of features to inscribe them */
        fingerprint.featuresInscription = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        /* Process fingerprint sample and create a set of features to verify them */
        fingerprint.featuresVerification = fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        /* Verify the qualifi of fingerprint sample and is added to its recruiter if is good */
        if (fingerprint.featuresInscription != null) {
            try {
                fingerprint.recruiter.addFeatures(fingerprint.featuresInscription); 

                /* Draw fingerprint captured */
                Image image = fingerprint.createFingerprintImage(sample);
                fingerprintForm.drawFingerprint(image);

            } catch (DPFPImageQualityException ex) {
                fingerprintForm.showMessage("Error: " + ex.getMessage());
            } finally {
                showFingerprintMissing();
                /* Verify if template have been created*/
                switch (fingerprint.recruiter.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY: // Capture successful
                        Student student = this.fingerprint.identifyFingerprint(students);
                        if(student == null){
                            this.fingerprintForm.showErrorMessage("Student not found in course " + course.toString());
                        }else{
                            this.fingerprintForm.showInfoMessage("Student found!:\n" + student.allToString());
                        }
                        this.clear();
                        break;

                    case TEMPLATE_STATUS_FAILED: // Capture failed
                        fingerprint.recruiter.clear();
                        fingerprintForm.showErrorMessage("Template cannot be created, try again");
                        this.clear();
                        break;
                    case TEMPLATE_STATUS_UNKNOWN:
                        fingerprint.recruiter.clear();
                        fingerprintForm.showErrorMessage("Fingerprint sample unknown, try again");
                        this.clear();
                        break;

                }
            }
        }
    }

    public void start(){
        this.fingerprintForm.showMessage("------READER STARTED--------");
        this.fingerprintForm.enableBtnStop(true);
        this.fingerprintForm.enableBtnParticipations(false);
        this.fingerprintForm.enableBtnSave(false);
        this.fingerprintForm.enableBtnIdentify(false);
        this.fingerprintForm.enableBtnAssistance(false);
        this.fingerprintForm.enableCmbCourseGroups(false);
        this.fingerprint.reader.startCapture();
    }
    
    public void stop(){
        this.fingerprintForm.showMessage("------READER STOPPED--------");
        this.fingerprintForm.enableBtnStop(false);
        /* Fix bug before to enable course groups:
        
            The thread "this.fingerprint.reader.addDataListener" does not stop
            when this.fingerprint.reader.stopCapture() is called.
            Search how to stop thread.
        */
        // this.fingerprintForm.enableBtnParticipations(true);
        // this.fingerprintForm.enableBtnAssistance(true);
        // this.fingerprintForm.enableCmbCourseGroups(true);
        this.fingerprint.reader.stopCapture();
        this.fingerprintForm.showInfoMessage("Thank you! Execute again the app if you want to continue");
        System.exit(0);
    }
    
    public void clear(){
        this.fingerprint.recruiter.clear();
        this.fingerprint.setTemplate(null);
    }

    public void process() {
        /** Thread that will get reader status */
        this.fingerprint.reader.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Fingerprint sensor is actived or connected");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Fingerprint sensor is deactived or disconnected");
                    }
                });
            }
        });

        /** Thread of sensor */
        this.fingerprint.reader.addSensorListener(new DPFPSensorAdapter() {// hilo del sensor
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Finger is placed on fingerprint reader");
                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Finger has been removed from fingerprint reader");
                    }
                });
            }
        });

        /** Thread of posible errors */
        this.fingerprint.reader.addErrorListener(new DPFPErrorAdapter() {
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Error: " + e.getError());
                    }
                });
            }
        });
    }

}
