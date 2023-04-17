.class public Lcom/example/test;
.super Landroid/support/v7/app/AppCompatActivity;
.source "test.java"


# static fields
.field private static final mUserAreaDir:Ljava/io/File;


# instance fields
.field i2:Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 23
    invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;

    move-result-object v0

    sput-object v0, Lcom/example/test;->mUserAreaDir:Ljava/io/File;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 22
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method private test2(F)V
    .registers 4
    .param p1, "a"    # F

    .prologue
    .line 35
    new-instance v0, Landroid/support/v7/widget/Toolbar;

    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;-><init>(Landroid/content/Context;)V

    .line 36
    .local v0, "toolbar":Landroid/support/v7/widget/Toolbar;
    const v1, 0x7f080089

    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setBackgroundResource(I)V

    .line 37
    return-void
.end method

.method private test3(I)V
    .registers 5
    .param p1, "i"    # I

    .prologue
    .line 43
    :goto_0
    :try_start_0
    new-instance v0, Ljava/io/File;

    const-string v1, ""

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 44
    .local v0, "file":Ljava/io/File;
    invoke-virtual {v0}, Ljava/io/File;->createNewFile()Z

    .line 46
    add-int/lit8 p1, p1, -0x1

    .line 47
    if-nez p1, :cond_16

    .line 48
    new-instance v1, Ljava/lang/RuntimeException;

    invoke-direct {v1}, Ljava/lang/RuntimeException;-><init>()V

    throw v1

    .line 56
    .end local v0    # "file":Ljava/io/File;
    :catch_14
    move-exception v1

    goto :goto_0

    .line 49
    .restart local v0    # "file":Ljava/io/File;
    :cond_16
    iget-object v2, p0, Lcom/example/test;->i2:Ljava/lang/Object;

    monitor-enter v2
    :try_end_19
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_19} :catch_14

    .line 51
    add-int/lit8 p1, p1, -0x2

    .line 55
    :try_start_1b
    monitor-exit v2

    goto :goto_0

    :catchall_1d
    move-exception v1

    monitor-exit v2
    :try_end_1f
    .catchall {:try_start_1b .. :try_end_1f} :catchall_1d

    :try_start_1f
    throw v1
    :try_end_20
    .catch Ljava/io/IOException; {:try_start_1f .. :try_end_20} :catch_14
.end method
