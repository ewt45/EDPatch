![icon](/patchapp/src/main/res/mipmap-xxhdpi/ic_launcher.png)

🌐[中文](../readme.md)\
🌐[English](./readme_en.md)\
🌐[Русский](./readme_ru.md)

## About
After adding some small functions to exagear (3.0.2) (mainly through modifying dex), I found it is too troublesome to manually change smali after all. \
Even if a tutorial telling the modified location and codes is provided, it is not suitable for most people who have no experience of apk modification.\
So I'm wondering if I can make an application. The user only needs to click a button and wait for the automatic modification to complete, then install the new apk. So this application was born. Due to my limited knowledge, 100% modification success or compatibility with all versions of exagear is not guaranteed.

- Video: [Youtube](https://youtu.be/t0y_AcWhZxI), [哔哩哔哩](https://www.bilibili.com/video/BV1mY411X7Nn/)
- Download: [release](https://github.com/ewt45/EDPatch/releases)
## Usage
### Steps
1. Select exagear apk from installed apks list or local files. Wait until decoded session completes.
2. Check the function you want to add. Function descriptions  are described below.
3. Click the "start patch" button and wait patientlly until building completes. The building info can be found in the log info.
4. After building the apk, click the "install patched apk" button to install it. Sigunature may change, so you might need to uninstalled the original installed apk.

### Attention
1. This apk is an experimental project, and it is not guaranteed that the function will be added successfully
2. After clicking"start patch" button, please don't click or move to other view, otherwise problems may occur.
3. The patched apk is located in  /storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/tmp_sign.apk. You can check it manually with third-party file manager app. 
4. Since the patched apk is resigned, you may need to uninstall the existing apks which use the same package name or sharedUserId (nornally the exagear and virgl overlay) before installing it. \
If you need virgl overlay, please resign them with the same signature manually.


## Available Functions
- [float action button](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  - Custom location of drive D
  - Custom Control
- [show cursor](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [custom resolution](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [android 11+ soft-input no-crashing](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [select obb manually](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)


## Third-party project dependencies
**These part of files are not uploaded yet, so compilation won't succeed.**
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)


## Change Log

### v0.0.2
- EDPatch interface and available functions support Russian now, thanks to Ēlochnik.
- Add a new function: Custom Control
  - Most of the options have descriptions that show up when long pressing. Select "default" of Control in container's properties. After entering the container, three-finger click to edit.
  - Mouse: Toggle visibility of mouse cursor. Switch gesture modes (default mode and touchpad mode) . Set mouse move speed.
  - Buttons: Customize buttons ( keys, text and position) . Switch layout mode (sidebar and free position) . Mouse button keys and joystick style buttons are available.
  - Style: Customize button color, transparency, size, sidebar background color.
- Update old functions: 
  - android 11+ soft-input no-crashing: Android 11 and above show/hide logic fix, use `toggleSoftInput()` method (In this way the input method can't hide by clicking from the popup menu, not a big problem, the android back key will be able to hide it). Change the time delay of calling out the input method from 1 second to 0.2 seconds.
  - select obb manually: The position of the text prompt is changed. Now after selecting the file, the text will be replaced by `obb selected, decompressing` or `the selected file is not obb`, in case the user selects the right obb without the correct prompt and selects something again. Display the selected file name as a toast. Disable the select button when unpacking.
  - Custom location of drive D: After the app starts, if there is no preset folder (default is `Exagear`), it will try to create it automatically.
  - custom resolution: Added multi-language support, probably fix the issue that crashing when container_prefs.xml contains a preferenceScreen of an external keyboard link.


### v0.0.1
- First release. Available functions: 
  - float action button (Custom location of drive D)
  - show cursor
  - custom resolution
  - android 11+ soft-input no-crashing
  - select obb manually