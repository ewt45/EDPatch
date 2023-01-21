.class public Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;
.super Ljava/lang/Object;
.source "AXSRendererGL.java"


# instance fields
.field private final rootCursorBitmap:Landroid/graphics/Bitmap;


# direct methods
.method public constructor <init>()V
    .registers 2

    .prologue
    .line 15
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 16
    invoke-static {}, Lcom/example/datainsert/exagear/cursor/CursorImage;->createBitmap()Landroid/graphics/Bitmap;

    move-result-object v0

    iput-object v0, p0, Lcom/eltechs/axs/widgets/viewOfXServer/AXSRendererGL;->rootCursorBitmap:Landroid/graphics/Bitmap;

    .line 18
    return-void
.end method

.method private createXCursorBitmap()Landroid/graphics/Bitmap;
    .registers 8

    .prologue
    const/4 v6, -0x1

    const/16 v5, 0xa

    .line 21
    :try_start_3
    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    invoke-virtual {v3}, Landroid/content/res/Resources;->getAssets()Landroid/content/res/AssetManager;

    move-result-object v3

    const-string v4, "mouse.png"

    invoke-virtual {v3, v4}, Landroid/content/res/AssetManager;->open(Ljava/lang/String;)Ljava/io/InputStream;

    move-result-object v3

    invoke-static {v3}, Landroid/graphics/BitmapFactory;->decodeStream(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
    :try_end_18
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_18} :catch_1a

    move-result-object v0

    .line 31
    :cond_19
    return-object v0

    .line 22
    :catch_1a
    move-exception v1

    .line 23
    .local v1, "e":Ljava/io/IOException;
    const-string v3, "TAG"

    const-string v4, "createXCursorBitmap: \u627e\u4e0d\u5230\u9f20\u6807\u56fe\u7247\uff0c\u8fd8\u662f\u7528\u00d7"

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 24
    invoke-virtual {v1}, Ljava/io/IOException;->printStackTrace()V

    .line 26
    sget-object v3, Landroid/graphics/Bitmap$Config;->ARGB_8888:Landroid/graphics/Bitmap$Config;

    invoke-static {v5, v5, v3}, Landroid/graphics/Bitmap;->createBitmap(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;

    move-result-object v0

    .line 27
    .local v0, "createBitmap":Landroid/graphics/Bitmap;
    const/4 v2, 0x0

    .local v2, "i":I
    :goto_2c
    if-ge v2, v5, :cond_19

    .line 28
    invoke-virtual {v0, v2, v2, v6}, Landroid/graphics/Bitmap;->setPixel(III)V

    .line 29
    rsub-int/lit8 v3, v2, 0x9

    invoke-virtual {v0, v2, v3, v6}, Landroid/graphics/Bitmap;->setPixel(III)V

    .line 27
    add-int/lit8 v2, v2, 0x1

    goto :goto_2c
.end method
