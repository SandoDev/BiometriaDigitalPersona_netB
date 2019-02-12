/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcesaHuella;

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
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import formularios.CapturaHuella;
import static formularios.CapturaHuella.TEMPLATE_PROPERTY;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Pen-SandO
 */
public class ProcesarHuella extends javax.swing.JFrame {

    private Dimension ds = new Dimension(450, 360);
    private Dimension cs = WebcamResolution.VGA.getSize();
    private Webcam wCam = Webcam.getDefault();
    private WebcamPanel wCamPanel = new WebcamPanel(wCam, ds, false);
    FileInputStream myStream;
    byte[] imageInBytes;
    private static final String USER_AGENT = "Mozilla/5.0";
    //private static final String SERVER_PATH = "http://localhost/";//la variable ya estaba definida
    String enlace = "";
    //parametros para la conexion con php
    //private static final String USER_AGENT = "Mozilla/5.0";
    private static final String SERVER_PATH = "http://localhost/BiometriaDigitalPerson/gestorHuella/";

    //se declara conn
    conectarMysqlMyadmin con = new conectarMysqlMyadmin();

    //Varible que permite iniciar el dispositivo de lector de huella conectado
    // con sus distintos metodos.
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();

    //Varible que permite establecer las capturas de la huellas, para determina sus caracteristicas
    // y poder estimar la creacion de un template de la huella para luego poder guardarla
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    //crear una nueva huella
    /**
     *
     */
    public DPFPFeatureSet featuresinscripcion;

    //verificar una huella ya existente
    /**
     *
     */
    public DPFPFeatureSet featuresverificacion;

    //Variable que para crear el template de la huella luego de que se hallan creado las caracteriticas
    // necesarias de la huella si no ha ocurrido ningun problema
    private DPFPTemplate template;

    //Esta variable tambien captura una huella del lector y crea sus caracteristcas para auntetificarla
    // o verificarla con alguna guardada en la BD
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();

    /**
     * Creates new form ProcesarHuella
     */
    public ProcesarHuella() {
        initComponents();
        this.setLocationRelativeTo(null);//aparece en medio de la pantalla
        wCam.setViewSize(cs);
        wCamPanel.setFillArea(true);
        panelCam.setLayout(new FlowLayout());
        panelCam.add(wCamPanel);
    }

    private static final class PlayerPanel extends JPanel {

        //private static final long serialVersionUID = 1L;
        private final BufferedImage images;
        private final Dimension size;
        //private int offset = 0;

        public PlayerPanel(BufferedImage images) {
            super();
            this.images = images;
            this.size = new Dimension(640, 480);
            setPreferredSize(size);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(images, 0, 0, null);
        }
    }

    public void mostrar(BufferedImage images) {
        //Dimension cs = WebcamResolution.VGA.getSize();
        //Webcam w = Webcam.getDefault();
        //w.setViewSize(cs);
        //w.open(true);

        JButton btnGuardar, btnBorrar;
        JTextField txt_nom;
        txt_nom = new JTextField();
        txt_nom.setBounds(670, 110, 100, 30);
        btnBorrar = new JButton("Tomar otra");
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(670, 50, 100, 40);
        btnBorrar.setBounds(670, 300, 100, 40);

        System.out.println("Tomando la foto");

        //images=w.getImage();
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String nom = txt_nom.getText();
                String link = "http://localhost/BiometriaDigitalPerson/BiometriaDigitalPersonan_php/gestorHuella/moduloPrestamo.php?user=" + nom;
                llamarPHP(link);
                sendPost(nom, enlace);

            }
        });

        //w.close();
        System.out.println("play");

        PlayerPanel panel = new PlayerPanel(images);

        JFrame f = new JFrame("Ejemplo de Foto");
        f.setMinimumSize(new Dimension(800, 540));
        f.add(btnGuardar);
        f.add(btnBorrar);
        f.add(txt_nom);
        f.add(panel);
        f.pack();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
        btnBorrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                File fichero = new File(enlace);
                eliminarFichero(fichero);

            }
        });
    }

    public void llamarPHP(String link) {

        URL url = null;
        try {
            url = new URL(link);
            try {
                Desktop.getDesktop().browse(url.toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

    }

    public static void eliminarFichero(File fichero) {

        if (!fichero.exists()) {
            System.out.println("El archivo data no existe.");
        } else {
            fichero.delete();
            System.out.println("El archivo data fue eliminado.");
        }

    }

    public void sendPost(String nombre, String name) {
        /*
        try {
            myStream = new FileInputStream("src/Images/test.jpg");
        } catch (FileNotFoundException ex) {
            System.out.println("Hubo Errores"+ex.getMessage());
        }
        
        try {
            imageInBytes = IOUtils.toByteArray(myStream);
        } catch (IOException ex) {
            System.out.println("Hubo Errores"+ex.getMessage());
        }*/

        System.out.println("Datos Entrantes: \n" + nombre + "\n" + enlace);
        //Creamos un objeto JSON
        JSONObject jsonObj = new JSONObject();
        //Añadimos el nombre, apellidos y email del usuario

        jsonObj.put("img_nombre", nombre);
        jsonObj.put("img_contenido", enlace);
        //Creamos una lista para almacenar el JSON
        List l = new LinkedList();
        l.addAll(Arrays.asList(jsonObj));
        //Generamos el String JSON
        String jsonString = JSONValue.toJSONString(l);
        System.out.println("JSON GENERADO:");
        System.out.println(jsonString);
        System.out.println("");

        try {
            //Codificar el json a URL
            jsonString = URLEncoder.encode(jsonString, "UTF-8");
            //Generar la URL
            String url = SERVER_PATH + "listenPostC.php";
            //Creamos un nuevo objeto URL con la url donde queremos enviar el JSON
            URL obj = new URL(url);
            //Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Añadimos la cabecera
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            //Creamos los parametros para enviar
            String urlParameters = "json=" + jsonString;
            // Enviamos los datos por POST
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            //Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //Mostramos la respuesta del servidor por consola
            System.out.println(response);
            //cerramos la conexión
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelCam = new javax.swing.JPanel();
        panHuellas = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblImagenHuella = new javax.swing.JLabel();
        panBtns = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnSalir = new javax.swing.JButton();
        btnVerificar = new javax.swing.JButton();
        btnIdentificar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btStart = new javax.swing.JButton();
        btCapture = new javax.swing.JButton();
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
        panelCamLayout.setHorizontalGroup(
            panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelCamLayout.setVerticalGroup(
            panelCamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        panHuellas.setBackground(new java.awt.Color(255, 191, 191));
        panHuellas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital Capturada", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        panHuellas.setPreferredSize(new java.awt.Dimension(400, 270));
        panHuellas.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(lblImagenHuella, java.awt.BorderLayout.CENTER);

        panHuellas.add(jPanel2, java.awt.BorderLayout.CENTER);

        panBtns.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acciones", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        panBtns.setPreferredSize(new java.awt.Dimension(400, 190));
        panBtns.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(366, 90));

        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        btnVerificar.setText("Verificar");
        btnVerificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerificarActionPerformed(evt);
            }
        });

        btnIdentificar.setText("Identificar");
        btnIdentificar.setPreferredSize(new java.awt.Dimension(71, 23));
        btnIdentificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentificarActionPerformed(evt);
            }
        });

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btStart.setText("iniciar");
        btStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartActionPerformed(evt);
            }
        });

        btCapture.setText("capturar");
        btCapture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCaptureActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnVerificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnIdentificar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSalir))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btStart)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCapture)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVerificar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIdentificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btStart)
                    .addComponent(btCapture))
                .addGap(71, 71, 71))
        );

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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panBtns, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panHuellas, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelCam, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panHuellas, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                    .addComponent(panelCam, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(panBtns, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnVerificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerificarActionPerformed
        String nombre = JOptionPane.showInputDialog("Nombre a verificar:");
        int doc = Integer.parseInt(nombre);
        verificarHuella(doc);
        Reclutador.clear();
    }//GEN-LAST:event_btnVerificarActionPerformed

    private void btnIdentificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentificarActionPerformed
        try {
            identificarHuella();
            Reclutador.clear();
        } catch (IOException ex) {
            Logger.getLogger(CapturaHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnIdentificarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarHuella();
        Reclutador.clear();
        lblImagenHuella.setIcon(null);
        start();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // en el momento en que se inicia la ventana del programa
        Iniciar();
        start();
        EstadoHuellas();
        btnGuardar.setEnabled(false);
        btnIdentificar.setEnabled(false);
        btnVerificar.setEnabled(false);
        btnSalir.grabFocus();
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // en el momento que se cierra la ventana del programa
        stop();
    }//GEN-LAST:event_formWindowClosing

    private void btStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartActionPerformed
        // TODO add your handling code here:
        Thread t = new Thread() {
            @Override
            public void run() {
                wCamPanel.start();
            }
        };
        t.setDaemon(true);
        t.start();
    }//GEN-LAST:event_btStartActionPerformed

    private void btCaptureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCaptureActionPerformed
        // TODO add your handling code here:
        BufferedImage images = new BufferedImage(450, 360, BufferedImage.TYPE_INT_RGB);

        try {
            //File file = new File(String.format("src/Images/test.jpg"));
            String name = String.format("C:/Nueva_carpeta/foto%d.jpg", System.currentTimeMillis());

            //File file = new File("C:/Nueva carpeta/test.jpg");
            File file = new File(name);
            enlace = name;
            ImageIO.write(wCam.getImage(), "JPG", file);
            images = wCam.getImage();
            JOptionPane.showMessageDialog(this, "Guardado en: \n" + file.getAbsolutePath(), "camCap", 1);
            System.out.println(file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Hay error en: \n" + e.getMessage(), "camCap", 1);
        }
        mostrar(images);
    }//GEN-LAST:event_btCaptureActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProcesarHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProcesarHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProcesarHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProcesarHuella.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProcesarHuella().setVisible(true);
            }
        });
    }

    /**
     *
     */
    public void start() {
        Lector.startCapture();
        EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }//metodo que inicia la captura

    /**
     *
     */
    public void stop() {
        Lector.stopCapture();
        EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }//metodo que para la captura

    /**
     *
     * @param string
     */
    public void EnviarTexto(String string) {
        txtArea.append(string + "\n");
    }//da el mensaje en el text area

    /**
     *
     */
    public void EstadoHuellas() {
        EnviarTexto("Muestra de Huellas Necesarias para Guardar Template " + Reclutador.getFeaturesNeeded());
    }//cuenta las capturas que se necesitan para crear la plantilla de la huella

    /**
     *
     * @param sample
     * @return
     */
    public Image CrearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    /**
     *
     * @param image
     */
    public void DibujarHuella(Image image) {
        lblImagenHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblImagenHuella.getWidth(), lblImagenHuella.getHeight(),
                        Image.SCALE_DEFAULT)));
        repaint();
    }

    /**
     *
     * @param sample
     * @param purpose
     * @return
     */
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }//extrae las caracteristicas de la huella

    /**
     *
     * @param bais
     * @return
     */
    public byte[] read(ByteArrayInputStream bais) {
        byte[] array = new byte[bais.available()];
        try {
            bais.read(array);

        } catch (IOException ex) {
            Logger.getLogger(ProcesarHuella.class.getName()).log(Level.SEVERE, null, ex);
        }
        return array;
    }//se intenta convertir la huella en un array de byte[]

    /**
     *
     */
    public void guardarHuella() {
        //Obtiene los datos del template de la huella actual
        ByteArrayInputStream datosHuella = new ByteArrayInputStream(template.serialize());
        Integer tamañoHuella = template.serialize().length;

        /*
        System.out.println("\n");
        byte[] bytes = read(datosHuella);
        for (int i = 0; i < bytes.length; i++) {
            System.out.println("Array[" + i + "] = " + bytes[i] + "\n");
        }
        System.out.println("\n");
        BitSet bitset = BitSet.valueOf(bytes);
        System.out.println("Length of bitset = " + bitset.length());
        /*for (int i = 0; i < bitset.length(); ++i) {
            System.out.println("bit " + i + ": " + bitset.get(i));
        }//se intenta convertir el array de bytes en array de bits
        System.out.println("\n");
        /*asi se define el array en json si se puede converitr a datosHuella en un array de bits se podria enviar
        {
            "studenst":[
                "jabe",
                "bob",
                "maik"
            ]
        }
        {
            "huehuella":[
            0:[
                0,
                0,
                1,
                0,
                1,
                1,
                0,
                0
            ],
            1:[
                0,
                0,
                1,
                0,
                1,
                1,
                0,
                0
            ]
        }
         */
        //Pregunta el nombre de la persona a la cual corresponde dicha huella
        String nombre = JOptionPane.showInputDialog("Ingrese nombre:");
        String doc = JOptionPane.showInputDialog("Ingrese numero de identificacion:");
        int doc2 = Integer.parseInt(doc);

        try {
            //Establece los valores para la sentencia SQL
            Connection c = con.getConnection(); //establece la conexion con la BD
            //realiza la insercion de los datos
            PreparedStatement guardarStmt = c.prepareStatement("INSERT INTO usuarios(doc,huella) values(?,?)");
            guardarStmt.setInt(1, doc2);
            guardarStmt.setBinaryStream(2, datosHuella, tamañoHuella);
            //Ejecuta la sentencia
            guardarStmt.execute();
            System.out.println("ejecutada la sentencia la sentencia");

            guardarStmt.close();
            JOptionPane.showMessageDialog(null, "Huella Guardada Correctamente");
            con.desconectar();
            btnGuardar.setEnabled(false);
            btnVerificar.grabFocus();
        } catch (SQLException ex) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al guardar los datos de la huella.");
        } finally {
            con.desconectar();
        }
        //enviando datos a php por medio de json
        sendPost(doc2, nombre);
    }

    /**
     *
     * @param doc2
     * @param nombre
     */
    public static void sendPost(int doc2, String nombre) {//queda pendiente pues no se sabe en que formato enviarle la huella digital
        //Creamos un objeto JSON
        JSONObject jsonObj = new JSONObject();
        //Añadimos el nombre, huella a json
        jsonObj.put("doc", doc2);
        jsonObj.put("nombre", nombre);

        //Creamos una lista para almacenar el JSON
        List l = new LinkedList();
        l.addAll(Arrays.asList(jsonObj));
        //Generamos el String JSON
        String jsonString = JSONValue.toJSONString(l);
        System.out.println("JSON GENERADO:");
        System.out.println(jsonString);
        System.out.println("");

        try {
            //Codificar el json a URL
            jsonString = URLEncoder.encode(jsonString, "UTF-8");
            //Generar la URL
            String url = SERVER_PATH + "listenPost.php";
            //Creamos un nuevo objeto URL con la url donde queremos enviar el JSON
            URL obj = new URL(url);
            //Creamos un objeto de conexión
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //Añadimos la cabecera
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            //Creamos los parametros para enviar
            String urlParameters = "json=" + jsonString;
            // Enviamos los datos por POST
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            //Capturamos la respuesta del servidor
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            //Mostramos la respuesta del servidor por consola
            System.out.println(response);
            //cerramos la conexión
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica la huella digital actual contra otra en la base de datos
     *
     * @param doc
     */
    public void verificarHuella(int doc) {
        try {
            //Establece los valores para la sentencia SQL
            Connection c = con.getConnection();
            //Obtiene la plantilla correspondiente a la persona indicada
            PreparedStatement verificarStmt = c.prepareStatement("SELECT huella FROM usuarios WHERE doc=?");
            verificarStmt.setInt(1, doc);
            ResultSet rs = verificarStmt.executeQuery();

            //Si se encuentra el nombre en la base de datos
            if (rs.next()) {
                //Lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("huella");
                //Crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
                setTemplate(referenceTemplate);

                // Compara las caracteriticas de la huella recientemente capturda con la
                // plantilla guardada al usuario especifico en la base de datos
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());

                //compara las plantilas (actual vs bd)
                if (result.isVerified()) {
                    JOptionPane.showMessageDialog(null, "Las huella capturada coinciden con la de " + doc, "Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No corresponde la huella con " + doc, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
                }

                //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje
            } else {
                JOptionPane.showMessageDialog(null, "No existe un registro de huella para " + doc, "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al verificar los datos de la huella.");
        } finally {
            con.desconectar();
        }
    }

    /**
     * Identifica a una persona registrada por medio de su huella digital
     *
     * @throws java.io.IOException
     */
    public void identificarHuella() throws IOException {
        try {
            //Establece los valores para la sentencia SQL
            Connection c = con.getConnection();

            //Obtiene todas las huellas de la bd
            PreparedStatement identificarStmt = c.prepareStatement("SELECT nombre,huella FROM usuarios");
            ResultSet rs = identificarStmt.executeQuery();

            //Si se encuentra el nombre en la base de datos
            while (rs.next()) {
                //Lee la plantilla de la base de datos
                byte templateBuffer[] = rs.getBytes("huella");
                String nombre = rs.getString("nombre");
                //Crea una nueva plantilla a partir de la guardada en la base de datos
                DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
                //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
                setTemplate(referenceTemplate);

                // Compara las caracteriticas de la huella recientemente capturda con la
                // alguna plantilla guardada en la base de datos que coincide con ese tipo
                DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());

                //compara las plantilas (actual vs bd)
                //Si encuentra correspondencia dibuja el mapa
                //e indica el nombre de la persona que coincidió.
                if (result.isVerified()) {
                    //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
                    JOptionPane.showMessageDialog(null, "Las huella capturada es de " + nombre, "Verificacion de Huella", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje
            JOptionPane.showMessageDialog(null, "No existe ningún registro que coincida con la huella", "Verificacion de Huella", JOptionPane.ERROR_MESSAGE);
            setTemplate(null);
        } catch (SQLException e) {
            //Si ocurre un error lo indica en la consola
            System.err.println("Error al identificar huella dactilar." + e.getMessage());
        } finally {
            con.desconectar();
        }
    }

    /**
     *
     */
    protected void Iniciar() {
        Lector.addDataListener(new DPFPDataAdapter() {//hilo que obtendra los datos
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("La Huella Digital ha sido Capturada");
                        ProcesarCaptura(e.getSample());//se procesa la captura de la huella
                    }
                });
            }
        });

        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {//hilo que obtendra el estatus del lector
            @Override
            public void readerConnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
                    }
                });
            }
        });

        Lector.addSensorListener(new DPFPSensorAdapter() {//hilo del sensor
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                    }
                });
            }
        });

        Lector.addErrorListener(new DPFPErrorAdapter() {//hilo de los posibles errores
            public void errorReader(final DPFPErrorEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EnviarTexto("Error: " + e.getError());
                    }
                });
            }
        });
    }//metodo que inicia el proceso de hilos relacionados con el sensor

    /**
     *
     * @param sample
     */
    public void ProcesarCaptura(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null) {
            try {
                System.out.println("Las Caracteristicas de la Huella han sido creada");
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear

                // Dibuja la huella dactilar capturada.
                Image image = CrearImagenHuella(sample);
                DibujarHuella(image);

                btnVerificar.setEnabled(true);
                btnIdentificar.setEnabled(true);

            } catch (DPFPImageQualityException ex) {
                System.err.println("Error: " + ex.getMessage());
            } finally {
                EstadoHuellas();
                // Comprueba si la plantilla se ha creado.
                switch (Reclutador.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        stop();
                        setTemplate(Reclutador.getTemplate());
                        EnviarTexto("La Plantilla de la Huella ha Sido Creada, ya puede Verificarla o Identificarla");
                        btnIdentificar.setEnabled(true);
                        btnVerificar.setEnabled(true);
                        btnGuardar.setEnabled(true);
                        btnGuardar.grabFocus();
                        break;

                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        JOptionPane.showMessageDialog(ProcesarHuella.this, "La Plantilla de la Huella no pudo ser creada, Repita el Proceso", "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
                        start();
                        break;
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCapture;
    private javax.swing.JButton btStart;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnIdentificar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVerificar;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImagenHuella;
    private javax.swing.JPanel panBtns;
    private javax.swing.JPanel panHuellas;
    private javax.swing.JPanel panelCam;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables

    //getters y setters de nuestra plantilla
    /**
     *
     * @return
     */
    public DPFPTemplate getTemplate() {
        return template;
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
