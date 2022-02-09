/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


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
import java.util.List;

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
