package model;


import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class Student {

    private ByteArrayInputStream fingerPrintData;
    private Integer fingerPrintSize;
    
    int id;
    String name;
    String lastName;
    String email;
    String identification;
    String codeInstitutional;
    String phone;
    byte[] fingerprint;
    
    public Student(){}
    
    public Student(
        int id,
        String name,
        String lastName,
        String email,
        String identification,
        String codeInstitutional,
        String phone,
        byte[] fingerprint
    ){
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.identification = identification;
        this.codeInstitutional = codeInstitutional;
        this.phone = phone;
        this.fingerprint = fingerprint;
    }

    public ByteArrayInputStream getFingerprintData() {
        return this.fingerPrintData;
    }

    public Integer getFingerprintSize() {
        return this.fingerPrintSize;
    }

    public void setFingerprintData(ByteArrayInputStream fingerPrintData) {
        this.fingerPrintData = fingerPrintData;
    }

    public void setFingerprintSize(Integer fingerPrintSize) {
        this.fingerPrintSize = fingerPrintSize;
    }

    @Override
    public String toString(){
        return this.name + " " + this.lastName;
    }
    
    public String allToString() {
        return String.join("\n ", Arrays.asList(
            this.name + " " + this.lastName,
            "Identification: " + this.identification,
            "Institutional Code: " + this.codeInstitutional,
            "Email: " + this.email
        ));
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getCodeInstitutional() {
        return codeInstitutional;
    }

    public void setCodeInstitutional(String codeInstitutional) {
        this.codeInstitutional = codeInstitutional;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public byte[] getFingerprint() {
        return this.fingerprint;
    }

    public void setFingerprint(byte[] fingerprint) {
        this.fingerprint = fingerprint;
    }
}
