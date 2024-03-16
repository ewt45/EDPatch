[TOC]
# 手动选择OBB/内置OBB/从github下载原版OBB
内置obb位置在apk/assets/obb文件夹内 任意名字
## 3.2
com.winlator.core.OBBImageInstaller.smali 中，搜索contentDialog定位，删去三行，添加一行
```smali
# 删去这三行
new-instance v4, Lcom/winlator/core/OBBImageInstaller$$ExternalSyntheticLambda2;

invoke-direct {v4, p0}, Lcom/winlator/core/OBBImageInstaller$$ExternalSyntheticLambda2;-><init>(Lcom/winlator/MainActivity;)V

invoke-static {p0, v3, v4}, Lcom/winlator/contentdialog/ContentDialog;->confirm(Landroid/content/Context;ILjava/lang/Runnable;)V
# 添加这一行
invoke-static {p0}, Lcom/example/datainsert/winlator/all/OBBFinder;->extract_3_2(Lcom/winlator/MainActivity;)V
```

# xserver左侧抽屉栏添加额外内容
进入容器后，按手机返回键，可显示左侧抽屉栏。在里面添加一些额外功能。
1. 下载smali压缩包并添加。（适用于5.0的：https://wwk.lanzout.com/iZMKv1nf3utc）
## ~~pulseaudio~~,旋转屏幕
1. 编辑smali。com.winlator.XServerDisplayActivity类，onCreate方法, 搜索字符串 setupUI 定位，在下一行添加。用于在侧栏显示更多选项。
     ```
      invoke-static {p0}, Lcom/example/datainsert/winlator/all/XserverNavMenuControl;->addItems(Lcom/winlator/XServerDisplayActivity;)V
    ```
~~2. 向apk/assets中添加pulseaudio-xsdl.tar.zst 链接: https://pan.baidu.com/s/17BKYH4OzsPSysewXlyxDAQ?pwd=c94e 提取码: c94e~~
## 光标样式
1. 编辑smali。com.winlator.XServerDisplayActivity类，showInputControlsDialog方法末尾，ContentDialog.show()之前，添加一行。用于修改光标样式。
   ```
   #添加这一行
   invoke-static {p0, v0}, Lcom/example/datainsert/winlator/all/XserverNavMenuControl;->addInputControlsItems(Lcom/winlator/XServerDisplayActivity;Lcom/winlator/widget/ContentDialog;)V

   invoke-virtual {v0}, Lcom/winlator/widget/ContentDialog;->show()V
   ```
2. WindowAttributes类，getCursor方法，整个替换
   ```
   .method public getCursor()Lcom/winlator/xserver/Cursor;
     .registers 2

     .line 104
     sget-boolean v0, Lcom/example/datainsert/winlator/all/XserverNavMenuControl;->isGameStyleCursor:Z

     if-nez v0, :cond_7

     .line 105
     iget-object v0, p0, Lcom/winlator/xserver/WindowAttributes;->cursor:Lcom/winlator/xserver/Cursor;

     return-object v0

     .line 108
     :cond_7
     iget-object v0, p0, Lcom/winlator/xserver/WindowAttributes;->cursor:Lcom/winlator/xserver/Cursor;

     if-nez v0, :cond_20

     iget-object v0, p0, Lcom/winlator/xserver/WindowAttributes;->window:Lcom/winlator/xserver/Window;

     invoke-virtual {v0}, Lcom/winlator/xserver/Window;->getParent()Lcom/winlator/xserver/Window;

     move-result-object v0

     if-eqz v0, :cond_20

     .line 109
     iget-object v0, p0, Lcom/winlator/xserver/WindowAttributes;->window:Lcom/winlator/xserver/Window;

     invoke-virtual {v0}, Lcom/winlator/xserver/Window;->getParent()Lcom/winlator/xserver/Window;

     move-result-object v0

     iget-object v0, v0, Lcom/winlator/xserver/Window;->attributes:Lcom/winlator/xserver/WindowAttributes;

     invoke-virtual {v0}, Lcom/winlator/xserver/WindowAttributes;->getCursor()Lcom/winlator/xserver/Cursor;

     move-result-object v0

     return-object v0

     .line 110
     :cond_20
     iget-object v0, p0, Lcom/winlator/xserver/WindowAttributes;->cursor:Lcom/winlator/xserver/Cursor;

     return-object v0
   .end method
   ```

## 绝对位置点击(5.0)
显示在input control的弹窗中。现在已适配矩阵变化，无需拉伸全屏也可正确处理点击坐标。
1. 同上面光标样式的第一步（com.winlator.XServerDisplayActivity类），如果已经编辑过可跳过。
2. 添加smali https://wwk.lanzout.com/iXRWc1nf1b5c 如有同名则覆盖。该代码重新实现了TouchPadView


# 自定义wine修复
1. XServerDisplayActivity类，setupXEnvironment函数中，从上往下找到“WINEDEBUG"附近，然后按照下方提示修改。从`#开始修改`开始。该改动用于跳过未初始化的wininfo，container等成员变量的读取
```smali
    const-string v1, "WINEDEBUG"

    const-string v2, "-all"

    invoke-virtual {v0, v1, v2}, Lcom/winlator/core/EnvVars;->put(Ljava/lang/String;Ljava/lang/Object;)V
    
    #开始修改。判断是否为generate
    invoke-direct {p0}, Lcom/winlator/XServerDisplayActivity;->isGenerateWineprefix()Z

    move-result v0
    
    if-eqz v0, :cond_34
    #若generate wineprefix，则exec设置为“”
    const-string v1, ""
    goto :goto_45
    
    #若普通启动，跳到这，正常设置exec
    :cond_34
	#以下为原始代码。使劲往下翻，最后还要添加一行 :goto_45
    .line 266
    iget-object v0, p0, Lcom/winlator/XServerDisplayActivity;->wineInfo:Lcom/winlator/core/WineInfo;

    invoke-virtual {v0}, Lcom/winlator/core/WineInfo;->isWin64()Z

    move-result v0

    if-eqz v0, :cond_32

    const-string v0, "wine64"

    goto :goto_34

    :cond_32
    const-string v0, "wine"

    .line 267
    .local v0, "wineLoader":Ljava/lang/String;
    :goto_34
    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " explorer /desktop=shell,"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Lcom/winlator/XServerDisplayActivity;->xServer:Lcom/winlator/xserver/XServer;

    iget-object v2, v2, Lcom/winlator/xserver/XServer;->screenInfo:Lcom/winlator/xserver/ScreenInfo;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Lcom/winlator/XServerDisplayActivity;->createStartupBatchFile()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1
    
    #generate 设置为“”后跳到这。跳过原来的exec设置
    :goto_45

    .line 269
    .local v1, "guestExecutable":Ljava/lang/String;
```
2. XServerDisplayActivity类，lambda$generateWineprefix$10$com-winlator-XServerDisplayActivity函数，整体替换。 该改动用于等待全部操作（压缩wineprefix）执行完成后，再返回主界面。

```smali
.method synthetic lambda$generateWineprefix$10$com-winlator-XServerDisplayActivity(Ljava/io/File;Lcom/winlator/core/PreloaderDialog;Ljava/io/File;Ljava/lang/Integer;)V
    .registers 13
    .param p1, "installedWineDir"  # Ljava/io/File;
    .param p2, "preloaderDialog"  # Lcom/winlator/core/PreloaderDialog;
    .param p3, "rootDir"  # Ljava/io/File;
    .param p4, "status"  # Ljava/lang/Integer;

    .line 495
    :try_start_0
    invoke-static {}, Ljava/util/concurrent/Executors;->newSingleThreadExecutor()Ljava/util/concurrent/ExecutorService;

    move-result-object v0

    new-instance v7, Lcom/winlator/XServerDisplayActivity$$ExternalSyntheticLambda2;

    move-object v1, v7

    move-object v2, p0

    move-object v3, p4

    move-object v4, p1

    move-object v5, p2

    move-object v6, p3

    invoke-direct/range {v1 .. v6}, Lcom/winlator/XServerDisplayActivity$$ExternalSyntheticLambda2;-><init>(Lcom/winlator/XServerDisplayActivity;Ljava/lang/Integer;Ljava/io/File;Lcom/winlator/core/PreloaderDialog;Ljava/io/File;)V

    invoke-interface {v0, v7}, Ljava/util/concurrent/ExecutorService;->submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;

    move-result-object p1

    invoke-interface {p1}, Ljava/util/concurrent/Future;->get()Ljava/lang/Object;
    :try_end_17
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_17} :catch_18

    goto :goto_1c

    :catch_18
    move-exception p1

    invoke-virtual {p1}, Ljava/lang/Exception;->printStackTrace()V

    :goto_1c

    return-void
.end method
```


# 直装版 识别apk/assets中的数据包

1. FileUtils类的findOBBFile() 方法。替换为下面代码. obb文件位置固定为，filesdir/obb，版本号固定返回1 
   ```smali
   .method public static findOBBFile(Landroid/content/Context;Ljava/util/concurrent/atomic/AtomicReference;)I
   .registers 4
   .annotation system Ldalvik/annotation/Signature;
   value = {
   "(",
   "Landroid/content/Context;",
   "Ljava/util/concurrent/atomic/AtomicReference<",
   "Ljava/io/File;",
   ">;)I"
   }
   .end annotation
   
       .line 28
       new-instance v0, Ljava/io/File;
   
       invoke-virtual {p0}, Landroid/content/Context;->getFilesDir()Ljava/io/File;
   
       move-result-object p0
   
       const-string v1, "obb"
   
       invoke-direct {v0, p0, v1}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V
   
       invoke-virtual {p1, v0}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V
   
       const/4 p0, 0x1
   
       return p0
   .end method
   ```


2. TarZstdUtils 添加一个新extract方法。先将obb提取到filesdir/obb，然后再调用原extract方法解压数据包
   ```smali
   .method public static extract(Landroid/content/Context;Ljava/io/File;)Z
   .registers 6
   
       .line 24
       new-instance v0, Ljava/io/File;
   
       invoke-virtual {p0}, Landroid/content/Context;->getFilesDir()Ljava/io/File;
   
       move-result-object v1
   
       const-string v2, "obb"
   
       invoke-direct {v0, v1, v2}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V
   
       const/4 v1, 0x0
   
       .line 25
       :try_start_c
       invoke-virtual {p0}, Landroid/content/Context;->getAssets()Landroid/content/res/AssetManager;
   
       move-result-object p0
   
       invoke-virtual {p0, v2}, Landroid/content/res/AssetManager;->open(Ljava/lang/String;)Ljava/io/InputStream;
   
       move-result-object p0
       :try_end_14
       .catch Ljava/io/IOException; {:try_start_c .. :try_end_14} :catch_4b
   
       .line 26
       :try_start_14
       new-instance v2, Ljava/io/FileOutputStream;
   
       invoke-direct {v2, v0}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V
       :try_end_19
       .catchall {:try_start_14 .. :try_end_19} :catchall_3f
   
       .line 27
       :try_start_19
       invoke-static {p0, v2}, Lcom/winlator/core/StreamUtils;->copy(Ljava/io/InputStream;Ljava/io/OutputStream;)Z
   
       move-result v3
       :try_end_1d
       .catchall {:try_start_19 .. :try_end_1d} :catchall_35
   
       if-nez v3, :cond_28
   
       .line 29
       :try_start_1f
       invoke-virtual {v2}, Ljava/io/FileOutputStream;->close()V
       :try_end_22
       .catchall {:try_start_1f .. :try_end_22} :catchall_3f
   
       if-eqz p0, :cond_27
   
       :try_start_24
       invoke-virtual {p0}, Ljava/io/InputStream;->close()V
       :try_end_27
       .catch Ljava/io/IOException; {:try_start_24 .. :try_end_27} :catch_4b
   
       :cond_27
       return v1
   
       :cond_28
       :try_start_28
       invoke-virtual {v2}, Ljava/io/FileOutputStream;->close()V
       :try_end_2b
       .catchall {:try_start_28 .. :try_end_2b} :catchall_3f
   
       if-eqz p0, :cond_30
   
       :try_start_2d
       invoke-virtual {p0}, Ljava/io/InputStream;->close()V
       :try_end_30
       .catch Ljava/io/IOException; {:try_start_2d .. :try_end_30} :catch_4b
   
       .line 34
       :cond_30
       invoke-static {v0, p1}, Lcom/winlator/core/TarZstdUtils;->extract(Ljava/io/File;Ljava/io/File;)Z
   
       move-result p0
   
       return p0
   
       :catchall_35
       move-exception p1
   
       .line 25
       :try_start_36
       invoke-virtual {v2}, Ljava/io/FileOutputStream;->close()V
       :try_end_39
       .catchall {:try_start_36 .. :try_end_39} :catchall_3a
   
       goto :goto_3e
   
       :catchall_3a
       move-exception v0
   
       :try_start_3b
       invoke-virtual {p1, v0}, Ljava/lang/Throwable;->addSuppressed(Ljava/lang/Throwable;)V
   
       :goto_3e
       throw p1
       :try_end_3f
       .catchall {:try_start_3b .. :try_end_3f} :catchall_3f
   
       :catchall_3f
       move-exception p1
   
       if-eqz p0, :cond_4a
   
       :try_start_42
       invoke-virtual {p0}, Ljava/io/InputStream;->close()V
       :try_end_45
       .catchall {:try_start_42 .. :try_end_45} :catchall_46
   
       goto :goto_4a
   
       :catchall_46
       move-exception p0
   
       :try_start_47
       invoke-virtual {p1, p0}, Ljava/lang/Throwable;->addSuppressed(Ljava/lang/Throwable;)V
   
       :cond_4a
       :goto_4a
       throw p1
       :try_end_4b
       .catch Ljava/io/IOException; {:try_start_47 .. :try_end_4b} :catch_4b
   
       :catch_4b
       move-exception p0
   
       .line 30
       invoke-virtual {p0}, Ljava/io/IOException;->printStackTrace()V
   
       return v1
   .end method
   ```

3. MainActivity类，lambda$extractOBBImageIfNeeded$1$com-winlator-MainActivity 方法 改为新的extract方法
   ```smali
   # 注释掉这一行
   # invoke-static {v0, p1}, Lcom/winlator/core/TarZstdUtils;->extract(Ljava/io/File;Ljava/io/File;)Z
   # 改为这一行
   invoke-static {p0, p1}, Lcom/winlator/core/TarZstdUtils;->extract(Landroid/content/Context;Ljava/io/File;)Z

   ```
   
4. obb文件放入`apk/assets`中，文件名重命名为`obb`