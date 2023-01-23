![icon](/patchapp/src/main/res/mipmap-xxhdpi/ic_launcher.png)

## 介绍
靠着仅有的一点java基础知识，在为exagear(3.0.2)加了一些小功能之后（主要是通过修改dex实现），我发现手改smali终究还是太麻烦了。
即使提供了修改代码和位置的傻瓜式教程，还是有很多人因为完全不了解apk编辑/dex修改/smali语法而看不懂教程。
所以我在想，能否做一个自动修改apk的应用，用户完全不需要手动编辑smali，只需点一个按钮，等待修改完成后安装新的apk即可。于是本应用便诞生了。
由于能力有限，不保证适用于所有版本，不保证100%修改成功。

- 视频演示：[Youtube](https://youtu.be/t0y_AcWhZxI), [哔哩哔哩](https://www.bilibili.com/video/BV1mY411X7Nn/)
- 下载：[release](https://github.com/ewt45/EDPatch/releases)
## 使用
### 操作步骤
1. 从已安装应用列表，或者本地文件中选择exagear的apk，等待解包完成。
2. 勾选要添加的功能。有关各功能的详细介绍在下面。
3. 点击“开始修改”按钮，耐心等待打包完成。打包进度可以在输出信息中查看。
4. 打包完成后，点击“安装修改后的apk”按钮安装新apk。由于签名变化，可能需要卸载原先的apk。

### 注意事项：
1. 本apk为实验性项目，出现各种bug都是正常现象。
2. 在点击“开始修改”按钮后，请不要点击其他按钮或跳转其他界面，否则可能会出现问题。
3. 打包完成的apk存在于/storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/tmp_sign.apk，可以手动在第三方文件管理器中查看。
4. 由于打包后重新签名，需要卸载掉与其签名不同但包名或共享用户ID相同的应用（一般是各种版本的exagear和virgl overlay）才能安装新apk。如果需要virgl overlay，请去文件管理器中自己使用同一签名秘钥，手动对二者签名，再安装。

## 目前可添加的功能
- [悬浮操作按钮](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  -  修改d盘路径
- [强制显示鼠标光标](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [自定义分辨率](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [安卓11+调起输入法](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [手动选择obb](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)

## 项目第三方依赖
**用到的第三方项目都没传上来，编译是没法通过的。去release里下现成的apk就行了。**
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)
