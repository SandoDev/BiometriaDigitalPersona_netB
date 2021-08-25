/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProcessFingerprint;

import model.Fingerprint;
import controller.FingerprintController;
import view.FingerprintForm;


public class ProcessFingerprint {

    /* @param args the command line arguments */
    public static void main(String args[]) {

        Fingerprint model = new Fingerprint();
        FingerprintForm form = new FingerprintForm();
        FingerprintController controller = new FingerprintController(form, model);
        
        form.setController(controller);
        
        form.setDefaultCloseOperation(FingerprintForm.EXIT_ON_CLOSE);
        form.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                form.formWindowClosing();
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                form.formWindowOpened();
            }
        });
        form.setVisible(true);
        
    }
}
