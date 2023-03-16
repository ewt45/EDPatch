![icon](/patchapp/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## About
After adding some small functions to exagear (3.0.2) (mainly through modifying dex), I found it is too troublesome to manually change smali after all. \
Even if a tutorial telling the modified location and codes is provided, it is not suitable for most people who have no experience of apk modification.\
So I'm wondering if I can make an application. The user only needs to click a button and wait for the automatic modification to complete, then install the new apk. So this application was born. Due to my limited knowledge, 100% modification success or compatibility with all versions of exagear is not guaranteed.

- 视频演示：[Youtube](https://youtu.be/t0y_AcWhZxI), [哔哩哔哩](https://www.bilibili.com/video/BV1mY411X7Nn/)
- 下载：[release](https://github.com/ewt45/EDPatch/releases)
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


## Acknowledgement
Russian Translation：Ēlochnik
Many friends for testing and providing ideas
### Third-party project dependencies
**These part of files are not uploaded yet, so compilation won't succeed.**
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)



