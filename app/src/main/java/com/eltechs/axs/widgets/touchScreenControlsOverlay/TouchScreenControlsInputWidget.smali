.class public Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;
.super Landroid/view/View;
.source "TouchScreenControlsInputWidget.java"


# instance fields
.field private final MAX_FINGERS:I

.field private final TAG:Ljava/lang/String;

.field private final configuration:Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;

.field private final keyboard:Lcom/eltechs/axs/Keyboard;

.field private final mouse:Lcom/eltechs/axs/Mouse;

.field private touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

.field private final userFingers:[Lcom/eltechs/axs/Finger;

.field private final xServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .registers 3
    .param p1, "context"    # Landroid/content/Context;

    .prologue
    const/4 v0, 0x0

    .line 76
    invoke-direct {p0, p1, v0, v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;-><init>(Landroid/content/Context;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;)V

    .line 77
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;)V
    .registers 8
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "viewOfXServer"    # Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;
    .param p3, "touchScreenControlsInputConfiguration"    # Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;

    .prologue
    const/16 v1, 0xa

    const/4 v3, 0x1

    .line 62
    invoke-direct {p0, p1}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    .line 31
    const-string v0, "TouchScr16Widget"

    iput-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->TAG:Ljava/lang/String;

    .line 63
    iput v1, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->MAX_FINGERS:I

    .line 64
    new-array v0, v1, [Lcom/eltechs/axs/Finger;

    iput-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    .line 66
    const/4 v0, 0x0

    iput-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->xServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

    .line 67
    new-instance v0, Lcom/eltechs/axs/Mouse;

    new-instance v1, Lcom/eltechs/axs/PointerEventReporter;

    invoke-direct {v1, p2}, Lcom/eltechs/axs/PointerEventReporter;-><init>(Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)V

    invoke-direct {v0, v1}, Lcom/eltechs/axs/Mouse;-><init>(Lcom/eltechs/axs/PointerEventReporter;)V

    iput-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->mouse:Lcom/eltechs/axs/Mouse;

    .line 68
    new-instance v0, Lcom/eltechs/axs/Keyboard;

    new-instance v1, Lcom/eltechs/axs/KeyEventReporter;

    iget-object v2, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->xServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

    invoke-direct {v1, v2}, Lcom/eltechs/axs/KeyEventReporter;-><init>(Lcom/eltechs/axs/xserver/ViewFacade;)V

    invoke-direct {v0, v1}, Lcom/eltechs/axs/Keyboard;-><init>(Lcom/eltechs/axs/KeyEventReporter;)V

    iput-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->keyboard:Lcom/eltechs/axs/Keyboard;

    .line 69
    iput-object p3, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->configuration:Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;

    .line 70
    invoke-virtual {p0, v3}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->setFocusable(Z)V

    .line 71
    invoke-virtual {p0, v3}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->setFocusableInTouchMode(Z)V

    .line 72
    invoke-direct {p0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->installKeyListener()V

    .line 73
    return-void
.end method

.method static synthetic access$000(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)Lcom/eltechs/axs/Keyboard;
    .registers 2
    .param p0, "x0"    # Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    .prologue
    .line 30
    iget-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->keyboard:Lcom/eltechs/axs/Keyboard;

    return-object v0
.end method

.method private handleTouchEvent(Landroid/view/MotionEvent;)Z
    .registers 14
    .param p1, "motionEvent"    # Landroid/view/MotionEvent;

    .prologue
    const/4 v11, 0x1

    const v10, 0x3fb33333    # 1.4f

    const/4 v9, 0x0

    const/16 v8, 0xa

    .line 172
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getActionIndex()I

    move-result v0

    .line 173
    .local v0, "actionIndex":I
    invoke-virtual {p1, v0}, Landroid/view/MotionEvent;->getPointerId(I)I

    move-result v4

    .line 174
    .local v4, "pointerId":I
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getActionMasked()I

    move-result v1

    .line 175
    .local v1, "actionMasked":I
    if-lt v4, v8, :cond_16

    .line 219
    :cond_15
    :goto_15
    return v11

    .line 178
    :cond_16
    const/4 v3, 0x0

    .line 179
    .local v3, "i":I
    packed-switch v1, :pswitch_data_aa

    :pswitch_1a
    goto :goto_15

    .line 182
    :pswitch_1b
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    new-instance v6, Lcom/eltechs/axs/Finger;

    invoke-virtual {p1, v0}, Landroid/view/MotionEvent;->getX(I)F

    move-result v7

    invoke-virtual {p1, v0}, Landroid/view/MotionEvent;->getY(I)F

    move-result v8

    invoke-direct {v6, v7, v8}, Lcom/eltechs/axs/Finger;-><init>(FF)V

    aput-object v6, v5, v4

    .line 183
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

    iget-object v6, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v6, v6, v4

    invoke-virtual {v5, v6}, Lcom/eltechs/axs/TouchScreenControls;->handleFingerDown(Lcom/eltechs/axs/Finger;)V

    goto :goto_15

    .line 187
    :pswitch_36
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v5, v5, v4

    if-eqz v5, :cond_15

    .line 188
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v5, v5, v4

    invoke-virtual {p1, v0}, Landroid/view/MotionEvent;->getX(I)F

    move-result v6

    invoke-virtual {p1, v0}, Landroid/view/MotionEvent;->getY(I)F

    move-result v7

    invoke-virtual {v5, v6, v7}, Lcom/eltechs/axs/Finger;->release(FF)V

    .line 189
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

    iget-object v6, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v6, v6, v4

    invoke-virtual {v5, v6}, Lcom/eltechs/axs/TouchScreenControls;->handleFingerUp(Lcom/eltechs/axs/Finger;)V

    .line 190
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aput-object v9, v5, v4

    goto :goto_15

    .line 195
    :goto_59
    :pswitch_59
    if-ge v3, v8, :cond_15

    .line 196
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v5, v5, v3

    if-eqz v5, :cond_81

    .line 197
    invoke-virtual {p1, v3}, Landroid/view/MotionEvent;->findPointerIndex(I)I

    move-result v2

    .line 198
    .local v2, "findPointerIndex":I
    if-ltz v2, :cond_84

    .line 199
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v5, v5, v3

    invoke-virtual {p1, v2}, Landroid/view/MotionEvent;->getX(I)F

    move-result v6

    mul-float/2addr v6, v10

    invoke-virtual {p1, v2}, Landroid/view/MotionEvent;->getY(I)F

    move-result v7

    mul-float/2addr v7, v10

    invoke-virtual {v5, v6, v7}, Lcom/eltechs/axs/Finger;->update(FF)V

    .line 200
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

    iget-object v6, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v6, v6, v3

    invoke-virtual {v5, v6}, Lcom/eltechs/axs/TouchScreenControls;->handleFingerMove(Lcom/eltechs/axs/Finger;)V

    .line 206
    .end local v2    # "findPointerIndex":I
    :cond_81
    :goto_81
    add-int/lit8 v3, v3, 0x1

    goto :goto_59

    .line 202
    .restart local v2    # "findPointerIndex":I
    :cond_84
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

    iget-object v6, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v6, v6, v3

    invoke-virtual {v5, v6}, Lcom/eltechs/axs/TouchScreenControls;->handleFingerUp(Lcom/eltechs/axs/Finger;)V

    .line 203
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aput-object v9, v5, v3

    goto :goto_81

    .line 210
    .end local v2    # "findPointerIndex":I
    :goto_92
    :pswitch_92
    if-ge v3, v8, :cond_15

    .line 211
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v5, v5, v3

    if-eqz v5, :cond_a7

    .line 212
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->touchScreenControls:Lcom/eltechs/axs/TouchScreenControls;

    iget-object v6, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aget-object v6, v6, v3

    invoke-virtual {v5, v6}, Lcom/eltechs/axs/TouchScreenControls;->handleFingerUp(Lcom/eltechs/axs/Finger;)V

    .line 213
    iget-object v5, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->userFingers:[Lcom/eltechs/axs/Finger;

    aput-object v9, v5, v3

    .line 215
    :cond_a7
    add-int/lit8 v3, v3, 0x1

    goto :goto_92

    .line 179
    :pswitch_data_aa
    .packed-switch 0x0
        :pswitch_1b
        :pswitch_36
        :pswitch_59
        :pswitch_92
        :pswitch_1a
        :pswitch_1b
        :pswitch_36
    .end packed-switch
.end method

.method private installKeyListener()V
    .registers 2

    .prologue
    .line 85
    new-instance v0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;

    invoke-direct {v0, p0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;-><init>(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)V

    invoke-virtual {p0, v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->setOnKeyListener(Landroid/view/View$OnKeyListener;)V

    .line 154
    return-void
.end method


# virtual methods
.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .registers 9
    .param p1, "motionEvent"    # Landroid/view/MotionEvent;

    .prologue
    const/4 v3, 0x1

    const/4 v4, 0x0

    .line 158
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getSource()I

    move-result v5

    and-int/lit16 v5, v5, 0x1002

    const/16 v6, 0x1002

    if-ne v5, v6, :cond_2c

    move v0, v3

    .line 159
    .local v0, "z":Z
    :goto_d
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getSource()I

    move-result v5

    and-int/lit16 v5, v5, 0x4002

    const/16 v6, 0x4002

    if-ne v5, v6, :cond_2e

    move v1, v3

    .line 160
    .local v1, "z2":Z
    :goto_18
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getSource()I

    move-result v5

    and-int/lit16 v5, v5, 0x2002

    const/16 v6, 0x2002

    if-ne v5, v6, :cond_30

    move v2, v3

    .line 161
    .local v2, "z3":Z
    :goto_23
    if-nez v0, :cond_27

    if-eqz v1, :cond_32

    .line 162
    :cond_27
    invoke-direct {p0, p1}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->handleTouchEvent(Landroid/view/MotionEvent;)Z

    move-result v3

    .line 167
    :goto_2b
    return v3

    .end local v0    # "z":Z
    .end local v1    # "z2":Z
    .end local v2    # "z3":Z
    :cond_2c
    move v0, v4

    .line 158
    goto :goto_d

    .restart local v0    # "z":Z
    :cond_2e
    move v1, v4

    .line 159
    goto :goto_18

    .restart local v1    # "z2":Z
    :cond_30
    move v2, v4

    .line 160
    goto :goto_23

    .line 164
    .restart local v2    # "z3":Z
    :cond_32
    if-eqz v2, :cond_3b

    .line 165
    iget-object v3, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->mouse:Lcom/eltechs/axs/Mouse;

    invoke-virtual {v3, p1}, Lcom/eltechs/axs/Mouse;->handleMouseEvent(Landroid/view/MotionEvent;)Z

    move-result v3

    goto :goto_2b

    .line 167
    :cond_3b
    invoke-super {p0, p1}, Landroid/view/View;->onTouchEvent(Landroid/view/MotionEvent;)Z

    move-result v3

    goto :goto_2b
.end method
