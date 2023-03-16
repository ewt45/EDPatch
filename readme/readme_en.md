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
1. This apk is an experimental project, and it is not guaranteed that the function will be added successfully
2. After clicking"start patch" button, please don't click or move to other view, otherwise problems may occur.
3. The patched apk is located in  /storage/emulated/0/Android/data/com.ewt45.patchapp/files/patchtmp/tmp/dist/tmp_sign.apk. You can check it manually with third-party file manager app. 
4. Since the patched apk is resigned, you may need to uninstall the existing apks which use the same package name or sharedUserId (nornally the exagear and virgl overlay) before installing it. \
If you need virgl overlay, please resign them with the same signature manually.


## 目前可添加的功能
- [悬浮操作按钮](https://ewt45.github.io/blogs/2022/winter/exagearFab/) 
  -  修改d盘路径
- [强制显示鼠标光标](https://ewt45.github.io/blogs/2022/winter/exagearDefaultCursor/)
- [自定义分辨率](https://ewt45.github.io/blogs/2022/autumn/exagearCustomResl/)
- [安卓11+调起输入法](https://ewt45.github.io/blogs/2022/autumn/exagearKeyboard/)
- [手动选择obb](https://ewt45.github.io/blogs/2022/winter/exagearFindObb/)


## 鸣谢
感谢俄语翻译提供：Ēlochnik
### 第三方依赖
**用到的第三方项目都没传上来，编译是没法通过的。去release里下现成的apk就行了。**
- [apktool](https://ibotpeaches.github.io/Apktool/)
- [common-io](https://commons.apache.org/proper/commons-io/)
- [android-gif-drawable](https://github.com/koral--/android-gif-drawable)
- [apksig](https://android.googlesource.com/platform/tools/apksig)
- [AndroidBinaryXml](https://github.com/senswrong/AndroidBinaryXml)



