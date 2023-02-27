.class public Lcom/example/test;
.super Landroid/support/v7/app/AppCompatActivity;
.source "test.java"


# static fields
.field private static final mUserAreaDir:Ljava/io/File;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 22
    invoke-static {}, Lcom/example/datainsert/exagear/FAB/dialogfragment/DriveD;->getDriveDDir()Ljava/io/File;

    move-result-object v0

    sput-object v0, Lcom/example/test;->mUserAreaDir:Ljava/io/File;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 21
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    return-void
.end method

.method private test2(F)V
    .registers 4
    .param p1, "a"    # F

    .prologue
    .line 34
    new-instance v0, Landroid/support/v7/widget/Toolbar;

    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;-><init>(Landroid/content/Context;)V

    .line 35
    .local v0, "toolbar":Landroid/support/v7/widget/Toolbar;
    const v1, 0x7f08007d

    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setBackgroundResource(I)V

    .line 36
    return-void
.end method
