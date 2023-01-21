.class public Lcom/eltechs/axs/helpers/ZipInstallerObb;
.super Ljava/lang/Object;
.source "ZipInstallerObb.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lcom/eltechs/axs/helpers/ZipInstallerObb$Callbacks;
    }
.end annotation


# instance fields
.field private final callbacks:Lcom/eltechs/axs/helpers/ZipInstallerObb$Callbacks;

.field private final context:Landroid/content/Context;

.field private final exagearImage:Lcom/eltechs/axs/ExagearImageConfiguration/ExagearImage;

.field private foundObbVersion:I

.field private final isMain:Z

.field private final keepOldFiles:[Ljava/lang/String;

.field private final mayTakeFromSdcard:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;ZZLcom/eltechs/axs/ExagearImageConfiguration/ExagearImage;Lcom/eltechs/axs/helpers/ZipInstallerObb$Callbacks;[Ljava/lang/String;)V
    .registers 7
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "z"    # Z
    .param p3, "z2"    # Z
    .param p4, "exagearImage"    # Lcom/eltechs/axs/ExagearImageConfiguration/ExagearImage;
    .param p5, "callbacks"    # Lcom/eltechs/axs/helpers/ZipInstallerObb$Callbacks;
    .param p6, "strArr"    # [Ljava/lang/String;

    .prologue
    .line 67
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 68
    iput-object p1, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->context:Landroid/content/Context;

    .line 69
    iput-boolean p2, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->isMain:Z

    .line 70
    iput-boolean p3, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->mayTakeFromSdcard:Z

    .line 71
    iput-object p4, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->exagearImage:Lcom/eltechs/axs/ExagearImageConfiguration/ExagearImage;

    .line 72
    iput-object p5, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->callbacks:Lcom/eltechs/axs/helpers/ZipInstallerObb$Callbacks;

    .line 73
    iput-object p6, p0, Lcom/eltechs/axs/helpers/ZipInstallerObb;->keepOldFiles:[Ljava/lang/String;

    .line 75
    return-void
.end method

.method private checkObbUnpackNeed()Z
    .registers 2

    .prologue
    .line 48
    const/4 v0, 0x1

    return v0
.end method

.method private findObbFile()Ljava/io/File;
    .registers 2

    .prologue
    .line 52
    sget-object v0, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->obbFile:Ljava/io/File;

    return-object v0
.end method


# virtual methods
.method public installImageFromObbIfNeeded()V
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 28
    const-string v0, "ZipInstallerObb"

    const-string v1, "installImageFromObbIfNeeded: \u6b64\u65f6\u5f00\u59cb\u539f\u89e3\u538b\u6570\u636e\u5305\u64cd\u4f5c"

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 29
    return-void
.end method

.method public installImageFromObbIfNeededNew()V
    .registers 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 36
    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->findObbFile()Ljava/io/File;

    move-result-object v0

    if-nez v0, :cond_3a

    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->checkObbUnpackNeed()Z

    move-result v0

    if-eqz v0, :cond_3a

    .line 37
    const-string v0, "ZipInstallerObb"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "\u663e\u793afragment\uff0cneed="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->checkObbUnpackNeed()Z

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, ",file="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->findObbFile()Ljava/io/File;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 38
    invoke-static {p0}, Lcom/example/datainsert/exagear/obb/ProcessInstallObb;->start(Lcom/eltechs/axs/helpers/ZipInstallerObb;)V

    .line 45
    :goto_39
    return-void

    .line 40
    :cond_3a
    const-string v0, "ZipInstallerObb"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "\u6b63\u5e38\u8d70installImageFromObbIfNeeded\uff0cneed="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->checkObbUnpackNeed()Z

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, ",file="

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->findObbFile()Ljava/io/File;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 41
    invoke-virtual {p0}, Lcom/eltechs/axs/helpers/ZipInstallerObb;->installImageFromObbIfNeeded()V

    goto :goto_39
.end method
