.class public Lcom/example/test;
.super Landroid/support/v7/app/AppCompatActivity;
.source "test.java"


# static fields
.field private static final mUserAreaDir:Ljava/io/File;

.field private static final staticAndFinal:Z = true

.field private static staticNotFinal:Z


# instance fields
.field i2:Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 25
    invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;

    move-result-object v0

    sput-object v0, Lcom/example/test;->mUserAreaDir:Ljava/io/File;

    .line 26
    const/4 v0, 0x0

    sput-boolean v0, Lcom/example/test;->staticNotFinal:Z

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 30
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    .line 32
    return-void
.end method

.method public static reflectInvoke(Lcom/eltechs/ed/fragments/ManageContainersFragment;)V
    .registers 11
    .param p0, "fragment"    # Lcom/eltechs/ed/fragments/ManageContainersFragment;

    .prologue
    const/4 v4, 0x0

    .line 62
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/Class;->getDeclaredClasses()[Ljava/lang/Class;

    move-result-object v5

    array-length v6, v5

    :goto_a
    if-ge v4, v6, :cond_53

    aget-object v0, v5, v4

    .line 64
    .local v0, "clz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const-class v7, Landroid/os/AsyncTask;

    invoke-virtual {v0}, Ljava/lang/Class;->getSuperclass()Ljava/lang/Class;

    move-result-object v8

    invoke-virtual {v7, v8}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_4b

    .line 66
    const/4 v7, 0x2

    :try_start_1b
    new-array v7, v7, [Ljava/lang/Class;

    const/4 v8, 0x0

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v9

    aput-object v9, v7, v8

    const/4 v8, 0x1

    sget-object v9, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    aput-object v9, v7, v8

    invoke-virtual {v0, v7}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v1

    .line 67
    .local v1, "constructor":Ljava/lang/reflect/Constructor;, "Ljava/lang/reflect/Constructor<Landroid/os/AsyncTask<Lcom/eltechs/ed/guestContainers/GuestContainer;Ljava/lang/Void;Ljava/lang/Void;>;>;"
    const/4 v7, 0x1

    invoke-virtual {v1, v7}, Ljava/lang/reflect/Constructor;->setAccessible(Z)V

    .line 68
    const/4 v7, 0x2

    new-array v7, v7, [Ljava/lang/Object;

    const/4 v8, 0x0

    aput-object p0, v7, v8

    const/4 v8, 0x1

    const/4 v9, 0x0

    invoke-static {v9}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v9

    aput-object v9, v7, v8

    invoke-virtual {v1, v7}, Ljava/lang/reflect/Constructor;->newInstance([Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Landroid/os/AsyncTask;

    .line 69
    .local v3, "task":Landroid/os/AsyncTask;, "Landroid/os/AsyncTask<Lcom/eltechs/ed/guestContainers/GuestContainer;Ljava/lang/Void;Ljava/lang/Void;>;"
    const/4 v7, 0x0

    new-array v7, v7, [Lcom/eltechs/ed/guestContainers/GuestContainer;

    invoke-virtual {v3, v7}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;
    :try_end_4b
    .catch Ljava/lang/NoSuchMethodException; {:try_start_1b .. :try_end_4b} :catch_56
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_1b .. :try_end_4b} :catch_58
    .catch Ljava/lang/IllegalAccessException; {:try_start_1b .. :try_end_4b} :catch_54
    .catch Ljava/lang/InstantiationException; {:try_start_1b .. :try_end_4b} :catch_4e

    .line 62
    .end local v1    # "constructor":Ljava/lang/reflect/Constructor;, "Ljava/lang/reflect/Constructor<Landroid/os/AsyncTask<Lcom/eltechs/ed/guestContainers/GuestContainer;Ljava/lang/Void;Ljava/lang/Void;>;>;"
    .end local v3    # "task":Landroid/os/AsyncTask;, "Landroid/os/AsyncTask<Lcom/eltechs/ed/guestContainers/GuestContainer;Ljava/lang/Void;Ljava/lang/Void;>;"
    :cond_4b
    :goto_4b
    add-int/lit8 v4, v4, 0x1

    goto :goto_a

    .line 70
    :catch_4e
    move-exception v2

    .line 72
    .local v2, "e":Ljava/lang/ReflectiveOperationException;
    :goto_4f
    invoke-virtual {v2}, Ljava/lang/ReflectiveOperationException;->printStackTrace()V

    goto :goto_4b

    .line 76
    .end local v0    # "clz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v2    # "e":Ljava/lang/ReflectiveOperationException;
    :cond_53
    return-void

    .line 70
    .restart local v0    # "clz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    :catch_54
    move-exception v2

    goto :goto_4f

    :catch_56
    move-exception v2

    goto :goto_4f

    :catch_58
    move-exception v2

    goto :goto_4f
.end method

.method private test2(F)V
    .registers 9
    .param p1, "a"    # F

    .prologue
    .line 80
    new-instance v2, Landroid/support/v7/widget/Toolbar;

    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v3

    invoke-direct {v2, v3}, Landroid/support/v7/widget/Toolbar;-><init>(Landroid/content/Context;)V

    .line 81
    .local v2, "toolbar":Landroid/support/v7/widget/Toolbar;
    const v3, 0x7f080093

    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setBackgroundResource(I)V

    .line 82
    const-class v1, Lcom/example/test;

    .line 84
    .local v1, "s":Ljava/lang/Class;, "Ljava/lang/Class<Lcom/example/test;>;"
    const/4 v3, 0x1

    :try_start_12
    new-array v3, v3, [Ljava/lang/Class;

    const/4 v4, 0x0

    const-class v5, Ljava/util/Map;

    aput-object v5, v3, v4

    invoke-virtual {v1, v3}, Ljava/lang/Class;->getDeclaredConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;

    move-result-object v3

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Object;

    const/4 v5, 0x0

    new-instance v6, Ljava/util/HashMap;

    invoke-direct {v6}, Ljava/util/HashMap;-><init>()V

    aput-object v6, v4, v5

    invoke-virtual {v3, v4}, Ljava/lang/reflect/Constructor;->newInstance([Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_2b
    .catch Ljava/lang/NoSuchMethodException; {:try_start_12 .. :try_end_2b} :catch_2c
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_12 .. :try_end_2b} :catch_33
    .catch Ljava/lang/IllegalAccessException; {:try_start_12 .. :try_end_2b} :catch_3a
    .catch Ljava/lang/InstantiationException; {:try_start_12 .. :try_end_2b} :catch_41

    .line 94
    return-void

    .line 85
    :catch_2c
    move-exception v0

    .line 86
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    new-instance v3, Ljava/lang/RuntimeException;

    invoke-direct {v3, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v3

    .line 87
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_33
    move-exception v0

    .line 88
    .local v0, "e":Ljava/lang/reflect/InvocationTargetException;
    new-instance v3, Ljava/lang/RuntimeException;

    invoke-direct {v3, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v3

    .line 89
    .end local v0    # "e":Ljava/lang/reflect/InvocationTargetException;
    :catch_3a
    move-exception v0

    .line 90
    .local v0, "e":Ljava/lang/IllegalAccessException;
    new-instance v3, Ljava/lang/RuntimeException;

    invoke-direct {v3, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v3

    .line 91
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :catch_41
    move-exception v0

    .line 92
    .local v0, "e":Ljava/lang/InstantiationException;
    new-instance v3, Ljava/lang/RuntimeException;

    invoke-direct {v3, v0}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v3
.end method

.method private test3(I)V
    .registers 5
    .param p1, "i"    # I

    .prologue
    .line 99
    :goto_0
    :try_start_0
    new-instance v0, Ljava/io/File;

    const-string v1, ""

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    .line 100
    .local v0, "file":Ljava/io/File;
    invoke-virtual {v0}, Ljava/io/File;->createNewFile()Z

    .line 102
    add-int/lit8 p1, p1, -0x1

    .line 103
    if-nez p1, :cond_16

    .line 104
    new-instance v1, Ljava/lang/RuntimeException;

    invoke-direct {v1}, Ljava/lang/RuntimeException;-><init>()V

    throw v1

    .line 112
    .end local v0    # "file":Ljava/io/File;
    :catch_14
    move-exception v1

    goto :goto_0

    .line 105
    .restart local v0    # "file":Ljava/io/File;
    :cond_16
    iget-object v2, p0, Lcom/example/test;->i2:Ljava/lang/Object;

    monitor-enter v2
    :try_end_19
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_19} :catch_14

    .line 107
    add-int/lit8 p1, p1, -0x2

    .line 111
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

.method private test_get_string_from_context_with_resid()V
    .registers 4

    .prologue
    .line 35
    const v1, 0x7f09452

    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v2

    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    move-result-object v0

    .line 38
    .local v0, "str":Ljava/lang/String;
    return-void
.end method

.method public static winlatorObb(Landroid/content/Context;Ljava/util/concurrent/atomic/AtomicReference;)I
    .registers 4
    .param p0, "context"    # Landroid/content/Context;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Ljava/util/concurrent/atomic/AtomicReference",
            "<",
            "Ljava/io/File;",
            ">;)I"
        }
    .end annotation

    .prologue
    .line 56
    .local p1, "result":Ljava/util/concurrent/atomic/AtomicReference;, "Ljava/util/concurrent/atomic/AtomicReference<Ljava/io/File;>;"
    new-instance v0, Ljava/io/File;

    const-string v1, "/this/should/not/exist"

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1, v0}, Ljava/util/concurrent/atomic/AtomicReference;->set(Ljava/lang/Object;)V

    .line 57
    const/4 v0, 0x1

    return v0
.end method


# virtual methods
.method public send_inputstream_instead_of_file()V
    .registers 3

    .prologue
    .line 53
    new-instance v0, Ljava/io/File;

    const-string v1, ""

    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    invoke-static {p0, v0}, Lcom/winlator/core/TarZstdUtils;->extract(Landroid/content/Context;Ljava/io/File;)Z

    .line 54
    return-void
.end method

.method public test_int_to_smali()V
    .registers 2

    .prologue
    .line 49
    const v0, 0x424242

    .line 50
    .local v0, "i":I
    return-void
.end method
