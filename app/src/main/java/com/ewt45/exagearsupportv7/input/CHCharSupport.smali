.class public Lcom/ewt45/exagearsupportv7/input/CHCharSupport;
.super Ljava/lang/Object;
.source "CHCharSupport.java"


# static fields
.field public static final avaiKeyCode:[Lcom/eltechs/axs/KeyCodesX;

.field public static currIndex:I


# direct methods
.method static constructor <clinit>()V
    .registers 4

    .prologue
    const/4 v3, 0x0

    .line 7
    const/16 v0, 0x1a

    new-array v0, v0, [Lcom/eltechs/axs/KeyCodesX;

    sget-object v1, Lcom/eltechs/axs/KeyCodesX;->KEY_A:Lcom/eltechs/axs/KeyCodesX;

    aput-object v1, v0, v3

    const/4 v1, 0x1

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_B:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x2

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_C:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x3

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_D:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x4

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_E:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x5

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_F:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x6

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_G:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/4 v1, 0x7

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_H:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x8

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_I:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x9

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_J:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xa

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_K:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xb

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_L:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xc

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_M:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xd

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_N:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xe

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_O:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0xf

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_P:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x10

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_Q:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x11

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_R:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x12

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_S:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x13

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_T:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x14

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_U:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x15

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_V:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x16

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_W:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x17

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_X:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x18

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_Y:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    const/16 v1, 0x19

    sget-object v2, Lcom/eltechs/axs/KeyCodesX;->KEY_Z:Lcom/eltechs/axs/KeyCodesX;

    aput-object v2, v0, v1

    sput-object v0, Lcom/ewt45/exagearsupportv7/input/CHCharSupport;->avaiKeyCode:[Lcom/eltechs/axs/KeyCodesX;

    .line 8
    sput v3, Lcom/ewt45/exagearsupportv7/input/CHCharSupport;->currIndex:I

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method
