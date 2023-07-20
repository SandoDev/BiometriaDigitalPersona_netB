; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "ITIUD - DAS BF"
#define MyAppVersion "1.0"
#define MyAppPublisher "ITIUD"
#define MyAppURL "https://das.itiud.org/docs/"
#define MyAppExeName "BiometriaDigitalPersona_netB.jar"
#define MyAppAssocName MyAppName + " File"
#define MyAppAssocExt ".jar"
#define MyAppAssocKey StringChange(MyAppAssocName, " ", "") + MyAppAssocExt

[Setup]
; NOTE: The value of AppId uniquely identifies this application. Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{B6A4C72D-0F0D-4662-8F3B-980D2D32081A}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
ChangesAssociations=yes
DisableProgramGroupPage=yes
LicenseFile=E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\LEEME.txt
InfoAfterFile=E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\POST-INSTALACION.txt
; Uncomment the following line to run in non administrative install mode (install for current user only.)
;PrivilegesRequired=lowest
OutputDir=E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0
OutputBaseFilename=Installer DAS-BF
SetupIconFile=E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\logo.ico
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\LEEME.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "E:\UD\DAS\BiometriaDigitalPersona_netB\src\releases\das-bf-v1.0.0\resources for scrip\POST-INSTALACION.txt"; DestDir: "{app}"; Flags: ignoreversion
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Registry]
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocExt}\OpenWithProgids"; ValueType: string; ValueName: "{#MyAppAssocKey}"; ValueData: ""; Flags: uninsdeletevalue
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}"; ValueType: string; ValueName: ""; ValueData: "{#MyAppAssocName}"; Flags: uninsdeletekey
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\{#MyAppExeName},0"
Root: HKA; Subkey: "Software\Classes\{#MyAppAssocKey}\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""
Root: HKA; Subkey: "Software\Classes\Applications\{#MyAppExeName}\SupportedTypes"; ValueType: string; ValueName: ".myp"; ValueData: ""

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

