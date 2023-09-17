

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