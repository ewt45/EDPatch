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
4. After building the apk, click the "install patched apk" button to install it.

### Attention
1. This apk is an experimental project, and it is not guaranteed that the function will be added successfully
2. After clicking"start patch" button, please don't click or move to other view, otherwise problems may occur.
3. The patched apk is located in  /storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/tmp_sign.apk. You can check it manually with third-party file manager app. 
4. If  the option " use default signature" in Settings is unchecked, after patching you need to uninstall the existing apks which use the same package name or sharedUserId (nornally the exagear and virgl overlay) first. If you need virgl overlay, please resign them with the same signature manually.


## Available Functions
- [float action button](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  - Custom location of drive D
  - Custom Control
  - PulseAudio (XSDL)
  - Xegw
- [show cursor](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [container settings - custom resolution](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [android 11+ soft-input no-crashing](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [select obb manually](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)
- launch exe shortcut directly
- MultiWine v2
- container settings - renderer

## Third-party project dependencies

[//]: # (**These part of files are not uploaded yet, so compilation won't succeed.**)
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)
- [Gson](https://github.com/google/gson)
- [org.tukaani.xz](https://tukaani.org/xz/)


## Change Log
### v0.1.1
- Old functions updated:
1. select obb manually
  - fix: clicking the blank space leads to an error of tmp.obb not found.
  - support bundled obb file (place it at `apk/assets/obb/*.obb` or `apk/lib/armeabi-v7a/libres.so`)
2. Custom location of drive D
  - Name changed: `Change Locations of Drives`
  - able to add multiple drives. All containers use the same config, config file saved at z:/opt/drives.txt
  - able to display more that one external device, named as "other storage device - (1,2...)". The old name "Sd card" is not accurate. Root folder is now added as an option for external devices.
3. Custom Control
  - fix: When editing one column of Left&Right side layout, reselect keys result in reset of key's order.

- Others: fix: custom resolution function doesn't read the installed version number correctly.

### v0.1.0
- Old functions updated:
1. container settings - renderers
  - fix: if lines start with # appears in the middle of txt, the lines below will not be read correctly.
  - txt supports adding undefined renderers.(copy a renderer lines, change its key, name and env) after added it will be shown in the options.
2. app shortcut
  - support icons. (if any)
3. show cursor
  - fix: in the last version(v0.0.6) assets/mouse.png is missing

### v0.0.6
- New functions available:
1. Xegw: In the gear button. Provide -legacy-drawing param to solve the problem of only a black screen with an arrow mouse displaying.

- Old functions updated:
1. pulseaudio: keep .config/pulse/deamon.conf while deleting other cache files.
2. Custom Control
  - First-person viewport movement: Renamed to Restrict mouse movement. Two options are shown when checked: Position-update interval and Limited distance. New mouse movement option for joystick buttons. Touchpad mode is also adjusted to limit or not the movement distance according to the settings.
  - Fix the problem of transparency lost when button is in long-press mode.
3. container settings - renderers:In renderer.txt, 'path' lines are changed to 'env' lines, so that more custom env params can be added by user，After upgrading this function, it is recommended to delete /opt/renderers.txt and then open the container settings once to see the formatting in the automatically generated txt.

- Others:
1. Disable Install button if there's no patched apk.

### v0.0.5
- New functions available:
1. PulseAudio (XSDL): PulseAudio is used to play audio, reducing sound problems. This function uses PulseAudio server extracted from Xserver XSDL. It requires 64-bit support on your device.

- Old functions updated:
1. float action button:
   - Long press to hide.
   - export logcat logs: If there's a folder named logcat in drive d, the logs will be saved inside this folder. Helpful when debugging.
2. container settings - renderer:
   - Multiwine v1 used to be combined with this function internally, but v2 no more. If v2 is patched, you have to patch this manually if you want to continue to use it.
   - Set diffrent LD_LIBRARY_PATH for different renderers. libGL.so.1 will be loaded from this path with higher priority. You can edit paths in z:/opt/renderers.txt.
   - Some renderer options have additional operations:
     - None turnip renderers: Redirect VK_ICD_FILENAMES to a non-exist file, in case these renderers won't work.
     - virgl built-in: Launch libvirgl_test_server.so in a new java process. Only works in xegw apk. No need of Mcat or /opt/start.sh. Log is outputed at Android/data/packageName/logs/virglLog.txt.
     - virtio-gpu: start Mcat. Before Xegw, mcat is used to start proot environment for this renderer, but in xegw it is rewriten to launch /opt/start.sh and won't launch proot automatically.

- Others:
1. apk size shrunk.


### v0.0.4

- MultiWIne v2 :Added a download icon on the left-top of  "containers manager" page, click to enter the "add/remove wines" page.
  - Local: Edit the downloaded or bundled Wines. After Clicking Install (extract) option, it will be displayed as 'active', which can be selected when creating a new container. Use Uninstall (delete extracted folder) option to reduce local storage. Wines are stored at z:/opt/WineCollection.
  - Downloadable: Download all kinds of Wines from the Internet. Available sources are WineHQ(Official build, only ubuntu18-builds are listed) and Kron4ek(shrinked size, staging versions are not included). Downloaded Wines appear at 'Local' page.
  - how to add bundled Wines into caches: wine binary file should appear at `/opt/WineCollection/custom/$TagFolder/$WineFolder/bin/wine`
    - `$TagFolder` : the folder's name represents this wine's name. it shows when creating containers, should be unique.
    - `$WineFolder` : it should contains the `./bin/wine` binary file.
    - also in `$TagFolder` there should be a `.tar.xz` archive file, from which `$WineFolder` will be extracted.
- container settings - renderer :
  - the renderer option is separated from multiwine now. multiwine v2 alone doesn't have the ability to set environment variables by renderer options in container settings. you need to add the function separately from edpatch.
  - what render options do:
    - each renderer has a diffrent path for libraries e.g.libGL.so.1, this path will be added as LD_LIBRARY_PATH=xxx so that you don't need to replace it at /usr/lib/i386-linux-gnu everytime. If you want to change this path, go to apk's dex -> ContainerSettingsFragment$renderEntries.smali
    - virgl overlay: add VTEST_WIN=1  VTEST_SOCK=
    - VirGL_built_in: run libvirgl_test_server.so from java process (without need of Mcat and /opt/start.sh) , logs output at /sdcard/virglLog.txt
    - virtio-gpu: try to start Mcat, which used to launch a proot environment for this renderer, but xegw replaces the old mcat so it won't work unless you put the old mcat.so back.
    - turnip dxvk: add GALLIUM_DRIVER=zink MESA_VK_WSI_DEBUG=sw 

### v0.0.3
- Now Sign apks with default signature. No need to uninstall original one or resign the patched apk manually , but it may be recognized as virus.
- New functions available: launch exe shortcut directly
  - Long press the app icon, select an exe shortcut, and click to run it.
  - How to add an app shortcut: Click the menu of an exe shortcut on the "Desktop" page, and select "Add as app shortcut".
  - Note: A maximum of four shortcuts can be added. Before launching from shortcut, ensure that the app is not running at background. After deleting the exe shortcut ( .desktop file), the app shortcut will be automatically deleted the next time launching the app . This feature requires Android 7 and above.
- Old functions updated:
  - show cursor: now try to read the image from `z:/opt/mouse.png` first, then `apk/assets/mouse.png`.
### v0.0.2
- EDPatch interface and available functions support Russian now, thanks to Ēlochnik.
- New functions available: Custom Control
  - Most of the options have descriptions that show up when long pressing. Select "default" of Control in container's properties. After entering the container, three-finger click to edit.
  - Mouse: Toggle visibility of mouse cursor. Switch gesture modes (default mode and touchpad mode) . Set mouse move speed.
  - Buttons: Customize buttons ( keys, text and position) . Switch layout mode (sidebar and free position) . Mouse button keys and joystick style buttons are available.
  - Style: Customize button color, transparency, size, sidebar background color.
- Old functions updated: 
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