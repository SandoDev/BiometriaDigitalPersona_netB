# Dactilar Digital Persona 4500
Dactilar Digital Persona 4500 is a desktop application to manage fingerprint reader.

## Getting started
### Prerequisites:

| Item| Specifications|
|--|--|
| Operative System | Windows |
| IDE | [NetBeans](https://netbeans.org/downloads/8.2/rc/) |
| Language | [Java](https://www.java.com/es/download/) |
| JDK| [8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) |
| Database| [MySQL](https://dev.mysql.com/downloads/mysql/)|
| Fingerprint Reader | Digital Persona 4500 Drivers |


#### Libs

- [Json Simple 1.1](http://www.java2s.com/Code/Jar/j/Downloadjsonsimple11jar.htm)
- [Webcam Capture 0.3.12](https://github.com/sarxos/webcam-capture/releases)
- [MySQL Connector / J 5.1.49](https://dev.mysql.com/downloads/connector/j/5.1.html)

  *One Touch for Windows SDK [(Link Suggested)](https://github.com/Eliezer090/JavaLibsPersonal/blob/master/JavaLibs.rar):*

- dpfpenrollment
- dpfpverification
- dpotapi
- dpotjni

#### Database config

1. Copy from /src/config/database.properties.example to /src/config/database.properties
2. Complete with database credencials


## Contact


| Role| Name| Email |
|--|--|--|
| Developer| David Tabla | [d2davidtb@gmail.com](mailto:d2davidtb@gmail.com)
| Developer| Camilo Sandoval | [camilo.sandoval.ad@gmail.com](mailto:camilo.sandoval.ad@gmail.com)

by [ITI](http://itiud.org) research group of [Universidad Distrital Francisco José de Caldas](https://www.udistrital.edu.co/en/index)


# To install in ubuntu:

- (Check) https://github.com/tadryanom/DigitalPersona-UareU-SDK-to-FreePascal
- Download drivers in ubuntu from fprint-demo
- Save jar files in java path

  jar cmvf manifest.mf dist/libs/*.jar