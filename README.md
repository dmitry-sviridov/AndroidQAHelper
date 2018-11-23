# AndroidQAHelper
Small utility for install/uninstall/clearing cache and data from several connected devices. 
Currentrly in development.


### Author: 
Dmitrij Kotsur  
Dmitrijkocur@gmail.com

### TODO: 
simplify architecture
### Before usage: 
install adb on your PC and update PATH enviroment variable.

### Usage:

Build project or use AndroidQAHelper.jar from repo. In this case, open cmd from project folder and print `java -jar AndroidQAHelper.jar`

- Connect devices through usb
- Press 'Refresh'
- Print your package name/Choose apk
- Click on functional radiobutton
- Click run.

You can run choosen job for one (selected in list) or all connected devices. Click on 'all devices' checkbox for setup job.

### Known issues:
After completing, UI doesn't refresh state. To refresh state click on device.
