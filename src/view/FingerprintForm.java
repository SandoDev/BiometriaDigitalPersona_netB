/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Image;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import controller.FingerprintController;
import model.CourseGroup;
import model.Student;
/**
 *
 * @author D2D
 */
public class FingerprintForm extends javax.swing.JFrame {
    
    private FingerprintController fingerprintController;

    /**
     * Creates new form Fingerprint
     */
    public FingerprintForm() {
        initComponents();
        this.setLocationRelativeTo(null); // Appear in middle of screen
        this.scrollHistory.setViewportView(txtAreaHistory);
        this.txtAreaHistory.setEditable(false);
        this.txtAreaHistory.setRows(5);
        this.txtAreaHistory.setColumns(20);
    }

    public void setCourseGroups(List<CourseGroup> courses)
    {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (CourseGroup course : courses) {
            model.addElement(course);
        }
        this.cmbCourseGroups.setModel(model);
    }

    public void setController(FingerprintController controller)
    {
        this.fingerprintController = controller;
    }
    
    /**
     * Show message in textArea
     * 
     * @param string
     */
    public void showMessage(String string) {
        txtAreaHistory.append(string + "\n");
    }
    
    public void getTemplateSuccessful() {
        btnIdentify.setEnabled(true);
        btnSave.setEnabled(true);
        btnSave.grabFocus();
    }
    
    public void getTemplateFailed() {
        JOptionPane.showMessageDialog(
            FingerprintForm.this,
            "Template cannot be created, try again",
            "Inscription of fingerprint",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(
            FingerprintForm.this,
            msg,
            "Action failed",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    public void showSuccessfulMessage(String msg) {
        JOptionPane.showMessageDialog(
            FingerprintForm.this,
            msg,
            "Action was successful",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    public void formWindowOpened() {
        this.fingerprintController.process();
        this.fingerprintController.start();
        showMessage("Quantity of missing fingerprints: " + this.fingerprintController.getFeaturesNeeded());
        btnSave.setEnabled(false);
        btnIdentify.setEnabled(false);
    }
    
    public void formWindowClosing() {
        this.fingerprintController.stop();
        showMessage("Fingerprint is not being used");
    }
    
    /**
     *
     * @param image
     */
    public void drawFingerprint(Image image) {
        
        ImageIcon imageIcon = new ImageIcon(
            image.getScaledInstance(
                    lblFingerprintImage.getWidth(),
                    lblFingerprintImage.getHeight(),
                    Image.SCALE_DEFAULT
            )
        );
        lblFingerprintImage.setIcon(imageIcon);
        repaint();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFingerprint = new javax.swing.JPanel();
        lblFingerprintImage = new javax.swing.JLabel();
        lblTitleFingerpringImage = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        btnIdentify = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        cmbCourseGroups = new javax.swing.JComboBox<>();
        btnLoadStudents = new javax.swing.JButton();
        panelHistory = new javax.swing.JPanel();
        scrollHistory = new javax.swing.JScrollPane();
        txtAreaHistory = new javax.swing.JTextArea();
        lblTitleHistory = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(722, 490));

        panelFingerprint.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblFingerprintImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTitleFingerpringImage.setText("Digital Fingerprint");

        javax.swing.GroupLayout panelFingerprintLayout = new javax.swing.GroupLayout(panelFingerprint);
        panelFingerprint.setLayout(panelFingerprintLayout);
        panelFingerprintLayout.setHorizontalGroup(
            panelFingerprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFingerprintLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFingerprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFingerprintImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTitleFingerpringImage, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelFingerprintLayout.setVerticalGroup(
            panelFingerprintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFingerprintLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleFingerpringImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFingerprintImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelButtons.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnIdentify.setText("Identify");
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

        cmbCourseGroups.setPreferredSize(new java.awt.Dimension(75, 25));
        cmbCourseGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCourseGroupsActionPerformed(evt);
            }
        });

        btnLoadStudents.setText("Load Students");
        btnLoadStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadStudentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbCourseGroups, 0, 426, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbCourseGroups, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLoadStudents, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelHistory.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtAreaHistory.setColumns(20);
        txtAreaHistory.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        txtAreaHistory.setRows(5);
        txtAreaHistory.setPreferredSize(new java.awt.Dimension(296, 84));
        txtAreaHistory.setRequestFocusEnabled(false);
        scrollHistory.setViewportView(txtAreaHistory);

        lblTitleHistory.setText("History");

        javax.swing.GroupLayout panelHistoryLayout = new javax.swing.GroupLayout(panelHistory);
        panelHistory.setLayout(panelHistoryLayout);
        panelHistoryLayout.setHorizontalGroup(
            panelHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelHistoryLayout.createSequentialGroup()
                        .addComponent(scrollHistory, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(panelHistoryLayout.createSequentialGroup()
                        .addComponent(lblTitleHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(133, 133, 133))))
        );
        panelHistoryLayout.setVerticalGroup(
            panelHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleHistory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollHistory, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelFingerprint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFingerprint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        this.fingerprintController.saveFingerprint();
        lblFingerprintImage.setIcon(null);
        this.fingerprintController.start();
        btnSave.setEnabled(false);
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentifyActionPerformed
        try {
            this.fingerprintController.identifyFingerprint();
            lblFingerprintImage.setIcon(null);
            this.fingerprintController.start();
            showMessage("Using fingerprint lector...");
        } catch (IOException ex) {
            Logger.getLogger(FingerprintForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnIdentifyActionPerformed

    private void cmbCourseGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCourseGroupsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCourseGroupsActionPerformed

    private void btnLoadStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadStudentsActionPerformed
        CourseGroup course = (CourseGroup) this.cmbCourseGroups.getModel().getSelectedItem();
        List<Student> students = this.fingerprintController.getStudents(course);
        if (students == null || students.isEmpty()) {
            this.showErrorMessage("Students not found");
        }else {
            students.forEach((Student student) -> {
                this.showMessage(student.toString());
            });
            this.showSuccessfulMessage("Students loaded");
        }
    }//GEN-LAST:event_btnLoadStudentsActionPerformed

    public static void start() {
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
            java.util.logging.Logger.getLogger(FingerprintForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FingerprintForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FingerprintForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FingerprintForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FingerprintForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnLoadStudents;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cmbCourseGroups;
    private javax.swing.JLabel lblFingerprintImage;
    private javax.swing.JLabel lblTitleFingerpringImage;
    private javax.swing.JLabel lblTitleHistory;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelFingerprint;
    private javax.swing.JPanel panelHistory;
    private javax.swing.JScrollPane scrollHistory;
    private javax.swing.JTextArea txtAreaHistory;
    // End of variables declaration//GEN-END:variables
}
