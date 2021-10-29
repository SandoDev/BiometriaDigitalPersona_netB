/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static java.lang.System.err;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Image;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author D2D
 */
public class Fingerprint {
    
    /** "con" var is an Database connection declared */
    Database con = new Database();
    
    /** "template" var allows create fingerprint template after features created */
    public DPFPTemplate template;
    
    public static final String TEMPLATE_PROPERTY = "template";

    /**
     * "checker" var captures fingerprint from reader and creates its features to
     * identify or verify it with someone saved in database
     */
    private DPFPVerification checker = DPFPGlobal.getVerificationFactory().createVerification();

    /** "featuresInscription" var verify that the existing fingerprint */
    public DPFPFeatureSet featuresVerification;
    
    /** "featuresInscription" var create a fingerprint */
    public DPFPFeatureSet featuresInscription;
    
    /** "reader" var allows init the Fingerprint reader device connected */
    public DPFPCapture reader = DPFPGlobal.getCaptureFactory().createCapture();
    
    /**
     * "recruiter" var allows stablish fingerprint captures, to determinate their
     * features and can estimate the creation of a fingerprint template and save it
     */
    public DPFPEnrollment recruiter = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    /**
     *
     * @return
     */
    public DPFPTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(DPFPTemplate template) {
        this.template = template;
    }
    /**
     * Extract fingerprint features
     * 
     * @param sample
     * @param purpose
     * @return
     */
    public DPFPFeatureSet extractFeatures(DPFPSample sample, DPFPDataPurpose purpose) {
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }
    
    /**
     *
     * @param sample
     * @return
     */
    public Image createFingerprintImage(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }
    
    public void identifyFingerprint() throws IOException {
        String title = "Fingerprint identification";
        try {
            // Establece los valores para la sentencia SQL
            Connection c = con.getConnection();

            String query = "SELECT name, lastName, identification, fingerprint FROM student";
            // Obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = c.prepareStatement(query);
            ResultSet rs = identificarStmt.executeQuery();

            while (rs.next()) {
                // Lee la plantilla de la base de datos
                String name = rs.getString("name");
                String lastName = rs.getString("lastName");
                // String doc = rs.getString("identification");
                byte templateBuffer[] = rs.getBytes("fingerprint");

                // Crea una nueva plantilla a partir de la guardada en la base de datos
                // si no llega a funcionar el boton identificar en un debido momento es que
                // porque hay huellas repetidas en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                // Envia la plantilla creada al objeto contendor de Template del componente de
                // huella digital
                setTemplate(referenceTemplate);

                // Compara las caracteriticas de la huella recientemente capturda con la
                // plantilla guardada al usuario especifico en la base de datos
                DPFPVerificationResult result = checker.verify(featuresVerification, getTemplate());

                // compara las plantilas (actual vs bd)
                // Si encuentra correspondencia dibuja el mapa
                // e indica el nombre de la persona que coincidió.
                if (result.isVerified()) {
                    String fullName = name + " " + lastName;
                    // crea la imagen de los datos guardado de las huellas guardadas en la base de
                    // datos
                    JOptionPane.showMessageDialog(null, "The fingerprint captured is of " + fullName, title,
                            JOptionPane.INFORMATION_MESSAGE);

                    return;
                }
            }
            // Si no encuentra alguna huella correspondiente al nombre lo indica con un
            // mensaje
            JOptionPane.showMessageDialog(null, "No existe ningún registro que coincida con la huella", title,
                    JOptionPane.ERROR_MESSAGE);
            this.setTemplate(null);
        } catch (SQLException e) {
            String message = "Error identifying fingerprint" + e.getMessage();
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        } finally {
            con.disconnect();
        }
    }
    
    public Student identifyFingerprint(List<Student> students){
        for(int i = 0; i < students.size(); i++){
            byte[] templateBuffer = students.get(i).getFingerprint();

            /* Create new template from the saved in database.*/
            DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
            
            /* Send template created to object container of componente template of fingerprint*/
            setTemplate(referenceTemplate);

            /* Compare features of fingerprint recently captured with tempalte saved to user*/
            DPFPVerificationResult result = checker.verify(featuresVerification, getTemplate());

            /* Compare templates current vs from database. If It is found, it draw fingerprint and return user. */
            if (result.isVerified()) {
                return students.get(i);
            }
        }
        return null;
    }
}
