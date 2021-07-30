/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessFingerprint;

import static java.lang.System.err;
import static java.lang.System.out;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import models.Database;
import models.Student;

/**
 *
 * @author Pen-SandO
 */
public class ProcessFingerprint extends javax.swing.JFrame {

    private int usuarioCod = 0;
    byte[] imageInBytes;
    private static final String USER_AGENT = "Mozilla/5.0";
    String enlace = "";
    private static final String SERVER_PATH = "http:// localhost/BiometriaDigitalPerson/gestorHuella/";

    /** "con" var is an Database connection declared */
    Database con = new Database();

    /** "reader" var allows init the Fingerprint reader device connected */
    private DPFPCapture reader = DPFPGlobal.getCaptureFactory().createCapture();

    /**
     * "recruiter" var allows stablish fingerprint captures, to determinate their
     * features and can estimate the creation of a fingerprint template and save it
     */
    private DPFPEnrollment recruiter = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    /** "featuresInscription" var create a fingerprint */
    public DPFPFeatureSet featuresInscription;

    /** "featuresInscription" var verify that the existing fingerprint */
    public DPFPFeatureSet featuresVerification;

    /** "template" var allows create fingerprint template after features created */
    private DPFPTemplate template;

    /**
     * "checker" var captures fingerprint from reader and creates its features to
     * identify or verify it with someone saved in database
     */
    private DPFPVerification checker = DPFPGlobal.getVerificationFactory().createVerification();

    public static final String TEMPLATE_PROPERTY = "template";

    /**
     * Creates new form ProcessFingerprint
     */
    public ProcessFingerprint() {
        initComponents();
        this.setLocationRelativeTo(null); // Appear in middle of screen
        txtArea.setEditable(false);
    }

    public void show(BufferedImage images) {
        JButton btnSave, btnDelete;
        JTextField txtNom;
        JLabel user = new JLabel();
        user.setText("Documento user");
        txtNom = new JTextField();
        txtNom.setBounds(670, 90, 100, 30);
        user.setBounds(670, 50, 100, 30);
        btnDelete = new JButton("Tomar otra");
        btnSave = new JButton("Prestar");
        btnSave.setBounds(670, 150, 100, 40);
        btnDelete.setBounds(670, 300, 100, 40);

        user.setVisible(true);
        txtNom.setEditable(false);
        txtNom.setText(String.valueOf(usuarioCod));
        out.println("Play");

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nom = txtNom.getText();
                String link = "http:// localhost/BiometriaDigitalPerson/BiometriaDigitalPersonan_php/gestorHuella/moduloPrestamo.php?user="
                        + nom;
                redirectToBrowser(link);
            }
        });
    }

    public void redirectToBrowser(String link) {

        URL url = null;
        try {
            url = new URL(link);
            Desktop.getDesktop().browse(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Check method
    public void sendPost(String nombre, String name) {
        System.out.println("Datos Entrantes: \n" + nombre + "\n" + enlace);
        // Creamos un objeto JSON
        JSONObject jsonObj = new JSONObject();
        // Añadimos el nombre, apellidos y email del usuario

        jsonObj.put("img_nombre", nombre);
        jsonObj.put("img_contenido", enlace);
        // Creamos una lista para almacenar el JSON
        List l = new LinkedList();
        l.addAll(Arrays.asList(jsonObj));
        // Generamos el String JSON
        String jsonString = JSONValue.toJSONString(l);
        System.out.println("JSON GENERADO:");
        System.out.println(jsonString);
        System.out.println("");

        try {
            // Codificar el json a URL
            jsonString = URLEncoder.encode(jsonString, "UTF-8");
            // Generar la URL
            String url = SERVER_PATH + "listenPostC.php";
            // Creamos un nuevo objeto URL con la url donde queremos enviar el JSON
            URL obj = new URL(url);
            // Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // Añadimos la cabecera
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Creamos los parametros para enviar
            String urlParameters = "json=" + jsonString;
            // Enviamos los datos por POST
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            // Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Mostramos la respuesta del servidor por consola
            System.out.println(response);
            // cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPost(int usuario, String dato, String formulario) {
        // Creamos un objeto JSON
        JSONObject jsonObj = new JSONObject();

        // Añadimos los datos necesarios al json
        jsonObj.put("dato_huella", dato);
        jsonObj.put("usuario", usuario);

        // Creamos una lista para almacenar el JSON
        List l = new LinkedList();
        l.addAll(Arrays.asList(jsonObj));

        // Generamos el String JSON
        String jsonString = JSONValue.toJSONString(l);
        System.out.println("JSON GENERADO:");
        System.out.println(jsonString);
        System.out.println("");

        try {
            // Codificar el json a URL
            jsonString = URLEncoder.encode(jsonString, "UTF-8");
            // Generar la URL
            String url = SERVER_PATH + formulario;
            // Creamos un nuevo objeto URL con la url donde queremos enviar el JSON
            URL obj = new URL(url);
            // Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // Añadimos la cabecera
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            // Creamos los parametros para enviar
            String urlParameters = "json=" + jsonString;
            // Enviamos los datos por POST
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            // Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // Mostramos la respuesta del servidor por consola
            System.out.println(response);
            // cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">// GEN-BEGIN:initComponents
    private void initComponents() {

        panelCam = new javax.swing.JPanel();
        panelFingerprint = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblFingerprintImage = new javax.swing.JLabel();
        panBtns = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtExit = new javax.swing.JButton();
        btnVerify = new javax.swing.JButton();
        btnIdentify = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btCapture = new javax.swing.JButton();
        terminaPrestamo = new javax.swing.JToggleButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        panelCam.setBackground(new java.awt.Color(0, 0, 0));
        panelCam.setForeground(new java.awt.Color(255, 255, 255));
        panelCam.setPreferredSize(new java.awt.Dimension(450, 360));

        javax.swing.GroupLayout panelCamLayout = new javax.swing.GroupLayout(panelCam);
        panelCam.setLayout(panelCamLayout);
        panelCamLayout.setHorizontalGroup(panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 447, Short.MAX_VALUE));
        panelCamLayout.setVerticalGroup(panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE));

        panelFingerprint.setBackground(new java.awt.Color(255, 191, 191));
        panelFingerprint.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital Capturada",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        panelFingerprint.setPreferredSize(new java.awt.Dimension(400, 270));
        panelFingerprint.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(lblFingerprintImage, java.awt.BorderLayout.CENTER);

        panelFingerprint.add(jPanel2, java.awt.BorderLayout.CENTER);

        panBtns.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acciones",
                javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        panBtns.setPreferredSize(new java.awt.Dimension(400, 190));
        panBtns.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(366, 90));

        txtExit.setText("Exit");
        txtExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtExitActionPerformed(evt);
            }
        });

        btnVerify.setText("Verify");
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });

        btnIdentify.setText("Identify");
        btnIdentify.setPreferredSize(new java.awt.Dimension(71, 23));
        btnIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentifyActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup().addComponent(btnVerify).addGap(18, 18, 18)
                                .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 99,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup().addComponent(btCapture).addGap(18, 18, 18)
                                .addComponent(terminaPrestamo)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtExit).addComponent(btnSave))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 287,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap()));
        jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        jPanel3Layout.createSequentialGroup().addGap(11, 11, 11)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btCapture).addComponent(terminaPrestamo).addComponent(txtExit,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(71, 71, 71))
                .addGroup(jPanel3Layout.createSequentialGroup().addComponent(jScrollPane2,
                        javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)));

        panBtns.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setLayout(new java.awt.BorderLayout());

        txtArea.setColumns(20);
        txtArea.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panBtns.add(jPanel4, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(panBtns, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(panelFingerprint, javax.swing.GroupLayout.PREFERRED_SIZE, 301,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(panelCam, javax.swing.GroupLayout.PREFERRED_SIZE, 447,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(panelCam, javax.swing.GroupLayout.DEFAULT_SIZE, 310,
                                                Short.MAX_VALUE)
                                        .addComponent(panelFingerprint, javax.swing.GroupLayout.DEFAULT_SIZE, 310,
                                                Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panBtns, javax.swing.GroupLayout.PREFERRED_SIZE, 190,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>// GEN-END:initComponents

    private void txtExitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtExitActionPerformed
        System.exit(0);
    }// GEN-LAST:event_txtExitActionPerformed

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnVerifyActionPerformed
        String nombre = JOptionPane.showInputDialog("Ingrese documento a verificar:");
        int doc = Integer.parseInt(nombre);
        verifyFingerprint(doc);
        recruiter.clear();
        lblFingerprintImage.setIcon(null);
        start();
    }// GEN-LAST:event_btnVerifyActionPerformed

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnIdentifyActionPerformed
        try {
            identifyFingerprint();
            recruiter.clear();
            lblFingerprintImage.setIcon(null);
            start();
        } catch (IOException ex) {
            Logger.getLogger(ProcessFingerprint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// GEN-LAST:event_btnIdentifyActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSaveActionPerformed
        saveFingerprint();
        recruiter.clear();
        lblFingerprintImage.setIcon(null);
        start();
    }// GEN-LAST:event_btnSaveActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        process();
        start();
        showFingerprintMissing();
        btnSave.setEnabled(false);
        btnIdentify.setEnabled(false);
        btnVerify.setEnabled(false);
        btCapture.setEnabled(false);
        terminaPrestamo.setEnabled(false);
        txtExit.grabFocus();
    }// GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosing
        // en el momento que se cierra la ventana del programa
        stop();
    }// GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
        // (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the default
         * look and feel. For details see http://
         * download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProcessFingerprint.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProcessFingerprint.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProcessFingerprint.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProcessFingerprint.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProcessFingerprint().setVisible(true);
            }
        });
    }

    /**
     * Start capture
     */
    public void start() {
        reader.startCapture();
        showMessage("Using Fingerprint Lector...");
    }

    /**
     * Stop capture
     */
    public void stop() {
        reader.stopCapture();
        showMessage("Fingerprint is not being used");
    }

    /**
     * Show message in textArea
     * 
     * @param string
     */
    public void showMessage(String string) {
        txtArea.append(string + "\n");
    }

    /**
     * Count captures necessaries to create fingerprint template
     */
    public void showFingerprintMissing() {
        showMessage("Quantity of missing fingerprints: " + recruiter.getFeaturesNeeded());
    }

    /**
     *
     * @param sample
     * @return
     */
    public Image createFingerprintImage(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    /**
     *
     * @param image
     */
    public void drawFingerprint(Image image) {
        lblFingerprintImage.setIcon(new ImageIcon(image.getScaledInstance(lblFingerprintImage.getWidth(),
                lblFingerprintImage.getHeight(), Image.SCALE_DEFAULT)));
        repaint();
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
     * Convert fingerprint in array of byte
     * 
     * @param bais
     * @return
     */
    public byte[] read(ByteArrayInputStream bais) {
        byte[] array = new byte[bais.available()];
        try {
            bais.read(array);

        } catch (IOException ex) {
            Logger.getLogger(ProcessFingerprint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }

    /**
     * Get data from template of current fingerprint
     */
    public void saveFingerprint() {
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
        String doc = JOptionPane.showInputDialog("Type identification number:");

        Student student = new Student();
        student.setFingerprintData(datosHuella);
        err.println(student.getFingerprintData());
        student.setFingerprintSize(template.serialize().length);
        Boolean result = student.saveFingerprint(doc);

        if (Boolean.TRUE.equals(result)) {
            JOptionPane.showMessageDialog(null, "Fingerprint saved correctly");
        } else {
            JOptionPane.showMessageDialog(null, "Fingerprint not saved!");
        }

        // TODO Create view in PHP component
        /**
         * String link = "http://
         * localhost/BiometriaDigitalPerson/BiometriaDigitalPersonan_php/gestorHuella/createUser.php?huella=huella-guardada-correctamente";
         * redirectToBrowser(link);
         * 
         */

        btCapture.setEnabled(true);
        btnSave.setEnabled(false);
        btnVerify.grabFocus();

    }

    /**
     * Verify current fingerprint with Student identification
     * 
     * @param doc
     */
    public void verifyFingerprint(int doc) {
        String title = "Verification of fingerprint";
        try {
            // Establece los valores para la sentencia SQL
            Connection c = con.getConnection();
            // Obtiene la plantilla correspondiente a la persona indicada
            PreparedStatement verificarStmt = c
                    .prepareStatement("SELECT fingerprint,name FROM student WHERE identification=?");
            verificarStmt.setInt(1, doc);
            ResultSet rs = verificarStmt.executeQuery();

            // Si se encuentra el nombre en la base de datos
            if (rs.next()) {
                // Lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("fingerprint");
                String name = rs.getString("name");
                // Crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                // Envia la plantilla creada al objeto contendor de Template del componente de
                // huella digital
                setTemplate(referenceTemplate);

                // Compara las caracteriticas de la huella recientemente capturda con la
                // plantilla guardada al usuario especifico en la base de datos
                DPFPVerificationResult result = checker.verify(featuresVerification, getTemplate());

                if (result.isVerified()) {
                    JOptionPane.showMessageDialog(null, "Las huella capturada coinciden con la de " + name, title,
                            JOptionPane.INFORMATION_MESSAGE);
                    btCapture.setEnabled(true);
                    usuarioCod = doc;
                } else {
                    JOptionPane.showMessageDialog(null, "No corresponde la huella con " + name, title,
                            JOptionPane.ERROR_MESSAGE);
                }

                // Si no encuentra alguna huella correspondiente al name lo indica con un
                // mensaje
            } else {
                JOptionPane.showMessageDialog(null, "No existe un registro de huella para " + doc, title,
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            String message = "Error verifying fingerprint data";
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            err.println(message);
        } finally {
            con.desconectar();
        }
    }

    /**
     * Identify a registered person by his fingerprint
     * 
     * @throws java.io.IOException
     */
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

                    btCapture.setEnabled(true);

                    return;
                }
            }
            // Si no encuentra alguna huella correspondiente al nombre lo indica con un
            // mensaje
            JOptionPane.showMessageDialog(null, "No existe ningún registro que coincida con la huella", title,
                    JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
        } catch (SQLException e) {
            String message = "Error identifying fingerprint";
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            err.println(message + e.getMessage());
        } finally {
            con.desconectar();
        }
    }

    /**
     *
     */
    protected void process() {
        /** Thread that will get data */
        reader.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Fingerprint captured");
                        /** Process fingerprint capture */
                        processCapture(e.getSample());
                    }
                });
            }
        });

        /** Thread that will get reader status */
        reader.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Fingerprint sensor is actived or connected");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Fingerprint sensor is deactived or disconnected");
                    }
                });
            }
        });

        /** Thread of sensor */
        reader.addSensorListener(new DPFPSensorAdapter() {// hilo del sensor
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Finger is placed on fingerprint reader");
                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Finger has been removed from fingerprint reader");
                    }
                });
            }
        });

        reader.addErrorListener(new DPFPErrorAdapter() {// hilo de los posibles errores
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        showMessage("Error: " + e.getError());
                    }
                });
            }
        });
    }// metodo que inicia el proceso de hilos relacionados con el sensor

    /**
     *
     * @param sample
     */
    public void processCapture(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con
        // el propósito de inscripción.
        featuresInscription = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con
        // el propósito de verificacion.
        this.featuresVerification = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su recruiter si
        // es bueno
        if (featuresInscription != null) {
            try {
                out.println("Fingerprint features created!");
                recruiter.addFeatures(featuresInscription);// Agregar las caracteristicas de la huella a la plantilla a
                                                           // crear

                // Dibuja la huella dactilar capturada.
                Image image = createFingerprintImage(sample);
                drawFingerprint(image);

            } catch (DPFPImageQualityException ex) {
                err.println("Error: " + ex.getMessage());
            } finally {
                showFingerprintMissing();
                // Comprueba si la plantilla se ha creado.
                switch (recruiter.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY: // informe de éxito y detiene la captura de huellas
                        stop();
                        setTemplate(recruiter.getTemplate());
                        showMessage("Template created, you can verify or identify it!");
                        btnIdentify.setEnabled(true);
                        btnVerify.setEnabled(true);
                        btnSave.setEnabled(true);
                        btnSave.grabFocus();
                        break;

                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reprocess la captura de huellas
                        recruiter.clear();
                        stop();
                        showFingerprintMissing();
                        setTemplate(null);
                        JOptionPane.showMessageDialog(ProcessFingerprint.this, "Templete cannot be created, try again",
                                "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
                        start();
                        break;
                }
            }
        }
    }

    // Variables declaration - do not modify// GEN-BEGIN:variables
    private javax.swing.JButton btCapture;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton txtExit;
    private javax.swing.JButton btnVerify;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblFingerprintImage;
    private javax.swing.JPanel panBtns;
    private javax.swing.JPanel panelFingerprint;
    private javax.swing.JPanel panelCam;
    private javax.swing.JToggleButton terminaPrestamo;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration// GEN-END:variables

    // getters y setters de nuestra plantilla
    /**
     *
     * @return
     */
    public DPFPTemplate getTemplate() {
        return this.template;
    }

    /**
     *
     * @param template
     */
    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
        this.template = template;
        firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

}
