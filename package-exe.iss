; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!
; This is still a work in progress

[Setup]
AppName=jDiskMark
AppVersion=0.5.1
DefaultDirName={pf}\jdiskmark-v0.5.1
DefaultGroupName=jdiskmark
; OutputDir=userdocs:Inno Setup Examples Output
OutputBaseFilename=install-jdiskmark-v0.5.1
; SetupIconFile=your_icon.ico
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin

[Files]
Source: "path\to\your\java\app\executable.exe"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\Your Java Application"; Filename: "{app}\executable.exe"

[Run]
Filename: "{app}\jDiskMark.exe"; Description: "{cm:LaunchProgram,MyApp}"; Flags: runascurrentuser nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\jDiskMark.exe"; Flags: runascurrentuser; Parameters: "-install -svcName ""jDiskMark"" -svcDesc ""jDiskMark"" -mainExe ""jDiskMark.exe""  "; Check: returnFalse()