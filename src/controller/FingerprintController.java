/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import static java.lang.System.err;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

import model.Student;
import model.Fingerprint;
import model.CourseGroup;
import DAO.CourseGroupDAO;
import DAO.StudentDAO;
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
     * Start capture
     */
    public void start() {
        this.fingerprint.reader.startCapture();
    }
    
    /**
     * Start capture
     */
    public void stop() {
        this.fingerprint.reader.stopCapture();
    }
    
    /**
     * Count captures necessaries to create fingerprint template
     */
    public void showFingerprintMissing() {
        this.fingerprintForm.showMessage("Quantity of missing fingerprints: " + this.fingerprint.recruiter.getFeaturesNeeded());
    }

    public void identifyFingerprint() throws IOException {
        this.fingerprint.identifyFingerprint();
        this.fingerprint.recruiter.clear();
        // TODO: Separate responsabilities
    }
    
    public int getFeaturesNeeded() {
        return this.fingerprint.recruiter.getFeaturesNeeded();
    }
    
    public void setCourseGroups() {
        try {
            CourseGroupDAO dao = new CourseGroupDAO();
            List<CourseGroup> courses = dao.getAll();
            this.fingerprintForm.setCourseGroups(courses);
        } catch(SQLException e){
            this.fingerprintForm.showMessage("An error ocurred consulting CourseGroups: "+e.getMessage());
        }
        
    }
    
    public List<Student> getStudents(CourseGroup course) {
        try {
            StudentDAO dao = new StudentDAO(course.getId());
            return dao.getAll();
        } catch(SQLException e){
            return new ArrayList<>();
        }
    }
    
    /**
     * Get data from template of current fingerprint
     */
    public void saveFingerprint() {
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(this.fingerprint.template.serialize());
        String doc = JOptionPane.showInputDialog("Type identification number:");

        Student student = new Student();
        student.setFingerprintData(datosHuella);
        err.println(student.getFingerprintData());
        student.setFingerprintSize(this.fingerprint.template.serialize().length);
        Boolean result = student.saveFingerprint(doc);

        if (Boolean.TRUE.equals(result)) {
            JOptionPane.showMessageDialog(null, "Fingerprint saved correctly");
        } else {
            JOptionPane.showMessageDialog(null, "Fingerprint not saved!");
        }
        
        this.fingerprint.recruiter.clear();

        // TODO Create view in PHP component
        /**
         * String link = "http://
         * localhost/BiometriaDigitalPerson/BiometriaDigitalPersonan_php/gestorHuella/createUser.php?huella=huella-guardada-correctamente";
         * redirectToBrowser(link);
         * 
         */

    }

    /**
     *
     */
    public void process() {
        /** Thread that will get data */
        this.fingerprint.reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Fingerprint captured");
                        /** Process fingerprint capture */
                        processCapture(e.getSample());
                    }
                });
            }
        });

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

        this.fingerprint.reader.addErrorListener(new DPFPErrorAdapter() {// hilo de los posibles errores
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        fingerprintForm.showMessage("Error: " + e.getError());
                    }
                });
            }
        });
    }
    
    /**
     *
     * @param sample
     */
    public void processCapture(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con
        // el propósito de inscripción.
        this.fingerprint.featuresInscription = this.fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con
        // el propósito de verificacion.
        this.fingerprint.featuresVerification = this.fingerprint.extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su recruiter si
        // es bueno
        if (this.fingerprint.featuresInscription != null) {
            try {
                this.fingerprint.recruiter.addFeatures(this.fingerprint.featuresInscription); 

                // Dibuja la huella dactilar capturada.
                Image image = this.fingerprint.createFingerprintImage(sample);
                this.fingerprintForm.drawFingerprint(image);

            } catch (DPFPImageQualityException ex) {
                err.println("Error: " + ex.getMessage());
            } finally {
                this.showFingerprintMissing();
                // Comprueba si la plantilla se ha creado.
                switch (this.fingerprint.recruiter.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY: // Capture successful
                        this.stop();
                        this.fingerprint.setTemplate(this.fingerprint.recruiter.getTemplate());
                        this.fingerprintForm.showMessage("Template created, you can verify or identify it!");
                        this.fingerprintForm.getTemplateSuccessful();
                        break;

                    case TEMPLATE_STATUS_FAILED: // Capture failed
                        this.fingerprint.recruiter.clear();
                        this.fingerprintForm.getTemplateFailed();
                        this.stop();
                        this.fingerprint.setTemplate(null);
                        this.start();
                        break;
                }
            }
        }
    }

}
