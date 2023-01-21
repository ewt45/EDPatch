.class public Lcom/example/datainsert/exagear/RSIDHelper;
.super Ljava/lang/Object;
.source "RSIDHelper.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 5
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static rslvID(II)I
    .registers 4
    .param p0, "my"    # I
    .param p1, "ori"    # I

    .prologue
    .line 14
    invoke-static {}, Lcom/eltechs/axs/Globals;->getAppContext()Landroid/content/Context;

    move-result-object v0

    invoke-virtual {v0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v0

    const-string v1, "com.ewt45.exagearsupportv7"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_11

    .end local p0    # "my":I
    :goto_10
    return p0

    .restart local p0    # "my":I
    :cond_11
    move p0, p1

    goto :goto_10
.end method
