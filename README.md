# AndroidQAHelper
Small utility for install/uninstall/clearing cache and data from several connected devices. 
Currentrly in development.

## Features:  
- install apk on multiple devices
- uninstall apk by packagename from multiple devices
- clean cache and data by packagename from multiple devices
- run app on devices
- run monkey test on devices

![alt text](https://user-images.githubusercontent.com/36419028/49168221-19274900-f348-11e8-8d69-f5cbe71cfab3.png)

### Author: 
Dmitry Sviridov 
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


