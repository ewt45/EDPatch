.class public Lcom/example/test;
.super Landroid/support/v7/app/AppCompatActivity;
.source "test.java"


# static fields
.field static final synthetic $assertionsDisabled:Z

.field private static final mUserAreaDir:Ljava/io/File;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 17
    const-class v0, Lcom/example/test;

    invoke-virtual {v0}, Ljava/lang/Class;->desiredAssertionStatus()Z

    move-result v0

    if-nez v0, :cond_12

    const/4 v0, 0x1

    :goto_9
    sput-boolean v0, Lcom/example/test;->$assertionsDisabled:Z

    .line 18
    invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;

    move-result-object v0

    sput-object v0, Lcom/example/test;->mUserAreaDir:Ljava/io/File;

    return-void

    .line 17
    :cond_12
    const/4 v0, 0x0

    goto :goto_9
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 17
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method private test2(F)V
    .registers 2
    .param p1, "a"    # F

    .prologue
    .line 29
    invoke-static {}, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->delCopiedObb()V

    .line 30
    return-void
.end method


# virtual methods
.method public onActivityResult(IILandroid/content/Intent;)V
    .registers 6
    .param p1, "requestCode"    # I
    .param p2, "resultCode"    # I
    .param p3, "data"    # Landroid/content/Intent;

    .prologue
    .line 32
    const/16 v1, 0x2711

    if-eq p1, v1, :cond_8

    .line 33
    invoke-static {p0, p1, p2, p3}, Lcom/example/datainsert/exagear/obb/SelectObbFragment;->receiveResultManually(Landroid/support/v7/app/AppCompatActivity;IILandroid/content/Intent;)V

    .line 39
    :goto_7
    return-void

    .line 36
    :cond_8
    sget-boolean v1, Lcom/example/test;->$assertionsDisabled:Z

    if-nez v1, :cond_17

    const/4 v1, 0x2

    if-eq p2, v1, :cond_17

    if-eqz p2, :cond_17

    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1

    .line 37
    :cond_17
    const/4 v0, 0x0

    .line 38
    .local v0, "i":I
    const/high16 v1, 0x3f800000    # 1.0f

    invoke-direct {p0, v1}, Lcom/example/test;->test2(F)V

    goto :goto_7
.end method
