.class public Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;
.super Ljava/lang/Object;
.source "TouchPadInterfaceOverlay.java"

# interfaces
.implements Lcom/eltechs/axs/activities/XServerDisplayActivityInterfaceOverlay;
.implements Lcom/eltechs/axs/activities/XServerDisplayActivityUiOverlaySidePanels;


# static fields
.field public static final buttonSizeInches:F = 0.4f

.field private static final buttonSzNormalDisplayInches:F = 0.45f

.field private static final buttonSzSmallDisplayInches:F = 0.4f

.field private static final displaySizeThresholdInches:F = 5.0f


# instance fields
.field private buttonHeight:I

.field private buttonWidth:I

.field private final buttonWidthPixelsFixup:I

.field private final controlsFactory:Lcom/eltechs/axs/TouchScreenControlsFactory;

.field private isToolbarsVisible:Z

.field private leftToolbar:Landroid/view/View;

.field protected mViewOfXServer:Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

.field protected mXServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

.field private rightToolbar:Landroid/view/View;

.field private tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;


# direct methods
.method public constructor <init>()V
    .registers 2

    .prologue
    .line 38
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 45
    const/16 v0, 0x1e

    iput v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidthPixelsFixup:I

    .line 46
    new-instance v0, Lcom/eltechs/axs/gamesControls/TouchPadScreenControlsFactory;

    invoke-direct {v0}, Lcom/eltechs/axs/gamesControls/TouchPadScreenControlsFactory;-><init>()V

    iput-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->controlsFactory:Lcom/eltechs/axs/TouchScreenControlsFactory;

    .line 47
    const/4 v0, 0x1

    iput-boolean v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    return-void
.end method

.method private createLeftScrollViewWithButtons(Landroid/app/Activity;Landroid/widget/LinearLayout;)V
    .registers 5
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "linearLayout"    # Landroid/widget/LinearLayout;

    .prologue
    .line 152
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_UP:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "\u2191"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 153
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_DOWN:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "\u2193"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 154
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_LEFT:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "\u2190"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 155
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_RIGHT:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "\u2192"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 156
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_A:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "A"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 157
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_B:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "B"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 158
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_C:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "C"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 159
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_D:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "D"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 160
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_E:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "E"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 161
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 162
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_G:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "G"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 163
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_H:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "H"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 164
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_I:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "I"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 165
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_J:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "J"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 166
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_K:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "K"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 167
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_L:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "L"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 168
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_M:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "M"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 169
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_N:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "N"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 170
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_O:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "O"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 171
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_P:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "P"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 172
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_Q:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Q"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 173
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_R:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "R"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 174
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_S:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "S"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 175
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_T:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "T"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 176
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_U:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "U"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 177
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_V:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "V"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 178
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_W:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "W"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 179
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_X:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "X"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 180
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_Y:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Y"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 181
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_Z:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Z"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 182
    return-void
.end method

.method private createLeftToolbar(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)Landroid/view/View;
    .registers 9
    .param p1, "xServerDisplayActivity"    # Lcom/eltechs/axs/activities/XServerDisplayActivity;
    .param p2, "viewOfXServer"    # Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

    .prologue
    .line 132
    invoke-virtual {p0, p1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createScrollView(Landroid/app/Activity;)Landroid/widget/LinearLayout;

    move-result-object v0

    .line 133
    .local v0, "createScrollView":Landroid/widget/LinearLayout;
    new-instance v3, Lcom/example/datainsert/exagear/controls/CursorToggle;

    invoke-direct {v3}, Lcom/example/datainsert/exagear/controls/CursorToggle;-><init>()V

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 134
    sget-object v3, Lcom/eltechs/axs/KeyCodesX;->KEY_SHIFT_LEFT:Lcom/eltechs/axs/KeyCodesX;

    const-string v4, "SHIFT"

    invoke-direct {p0, p1, v3, v4}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createStateButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v3

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 135
    sget-object v3, Lcom/eltechs/axs/KeyCodesX;->KEY_CONTROL_LEFT:Lcom/eltechs/axs/KeyCodesX;

    const-string v4, "CTRL"

    invoke-direct {p0, p1, v3, v4}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createStateButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v3

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 136
    sget-object v3, Lcom/eltechs/axs/KeyCodesX;->KEY_ALT_LEFT:Lcom/eltechs/axs/KeyCodesX;

    const-string v4, "ALT"

    invoke-direct {p0, p1, v3, v4}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createStateButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v3

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 137
    new-instance v2, Landroid/widget/ScrollView;

    invoke-direct {v2, p1}, Landroid/widget/ScrollView;-><init>(Landroid/content/Context;)V

    .line 138
    .local v2, "scrollView":Landroid/widget/ScrollView;
    new-instance v3, Landroid/view/ViewGroup$LayoutParams;

    const/4 v4, -0x2

    const/4 v5, -0x1

    invoke-direct {v3, v4, v5}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    invoke-virtual {v2, v3}, Landroid/widget/ScrollView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 139
    const-string v3, "#292c33"

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v3

    invoke-virtual {v2, v3}, Landroid/widget/ScrollView;->setBackgroundColor(I)V

    .line 140
    invoke-virtual {p0, p1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createScrollView(Landroid/app/Activity;)Landroid/widget/LinearLayout;

    move-result-object v1

    .line 141
    .local v1, "createScrollView2":Landroid/widget/LinearLayout;
    invoke-direct {p0, p1, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createLeftScrollViewWithButtons(Landroid/app/Activity;Landroid/widget/LinearLayout;)V

    .line 142
    invoke-virtual {v2, v1}, Landroid/widget/ScrollView;->addView(Landroid/view/View;)V

    .line 143
    invoke-virtual {v0, v2}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 144
    iget-boolean v3, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    if-nez v3, :cond_5b

    .line 145
    const/16 v3, 0x8

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->setVisibility(I)V

    .line 147
    :cond_5b
    iput-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->leftToolbar:Landroid/view/View;

    .line 148
    return-object v0
.end method

.method private createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;
    .registers 11
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "keyCodesX"    # Lcom/eltechs/axs/KeyCodesX;
    .param p3, "str"    # Ljava/lang/String;

    .prologue
    const/4 v3, 0x0

    .line 116
    new-instance v0, Lcom/eltechs/axs/StateButton;

    new-instance v2, Lcom/eltechs/axs/ButtonEventReporter;

    iget-object v1, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->mXServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

    const/4 v4, 0x1

    new-array v4, v4, [Lcom/eltechs/axs/KeyCodesX;

    aput-object p2, v4, v3

    invoke-direct {v2, v1, v4}, Lcom/eltechs/axs/ButtonEventReporter;-><init>(Lcom/eltechs/axs/xserver/ViewFacade;[Lcom/eltechs/axs/KeyCodesX;)V

    iget v4, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    iget v5, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonHeight:I

    move-object v1, p1

    move-object v6, p3

    invoke-direct/range {v0 .. v6}, Lcom/eltechs/axs/StateButton;-><init>(Landroid/content/Context;Lcom/eltechs/axs/ButtonEventReporter;ZIILjava/lang/String;)V

    return-object v0
.end method

.method private createRightScrollViewWithButtons(Landroid/app/Activity;Landroid/widget/LinearLayout;)V
    .registers 5
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "linearLayout"    # Landroid/widget/LinearLayout;

    .prologue
    .line 201
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_ESC:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Esc"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 202
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_RETURN:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Ren"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 203
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_SPACE:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Spa"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 204
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_TAB:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Tab"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 205
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_BACKSPACE:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "Bap"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 206
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_1:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "1"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 207
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_2:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "2"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 208
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_3:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "3"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 209
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_4:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "4"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 210
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_5:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "5"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 211
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_6:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "6"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 212
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_7:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "7"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 213
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_8:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "8"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 214
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_9:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "9"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 215
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_0:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "0"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 216
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F1:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F1"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 217
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F2:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F2"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 218
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F3:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F3"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 219
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F4:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F4"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 220
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F5:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F5"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 221
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F6:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F6"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 222
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F7:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F7"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 223
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F8:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F8"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 224
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F9:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F9"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 225
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F10:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F10"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 226
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F11:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F11"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 227
    sget-object v0, Lcom/eltechs/axs/KeyCodesX;->KEY_F12:Lcom/eltechs/axs/KeyCodesX;

    const-string v1, "F12"

    invoke-direct {p0, p1, v0, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createNormalButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;

    move-result-object v0

    invoke-virtual {p2, v0}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 228
    return-void
.end method

.method private createRightToolbar(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)Landroid/view/View;
    .registers 9
    .param p1, "xServerDisplayActivity"    # Lcom/eltechs/axs/activities/XServerDisplayActivity;
    .param p2, "viewOfXServer"    # Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

    .prologue
    .line 185
    invoke-virtual {p0, p1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createScrollView(Landroid/app/Activity;)Landroid/widget/LinearLayout;

    move-result-object v0

    .line 186
    .local v0, "createScrollView":Landroid/widget/LinearLayout;
    new-instance v2, Landroid/widget/ScrollView;

    invoke-direct {v2, p1}, Landroid/widget/ScrollView;-><init>(Landroid/content/Context;)V

    .line 187
    .local v2, "scrollView":Landroid/widget/ScrollView;
    new-instance v3, Landroid/view/ViewGroup$LayoutParams;

    const/4 v4, -0x2

    const/4 v5, -0x1

    invoke-direct {v3, v4, v5}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    invoke-virtual {v2, v3}, Landroid/widget/ScrollView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 188
    const-string v3, "#292c33"

    invoke-static {v3}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v3

    invoke-virtual {v2, v3}, Landroid/widget/ScrollView;->setBackgroundColor(I)V

    .line 189
    invoke-virtual {p0, p1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createScrollView(Landroid/app/Activity;)Landroid/widget/LinearLayout;

    move-result-object v1

    .line 190
    .local v1, "createScrollView2":Landroid/widget/LinearLayout;
    invoke-direct {p0, p1, v1}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createRightScrollViewWithButtons(Landroid/app/Activity;Landroid/widget/LinearLayout;)V

    .line 191
    invoke-virtual {v2, v1}, Landroid/widget/ScrollView;->addView(Landroid/view/View;)V

    .line 192
    invoke-virtual {v0, v2}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 193
    iget-boolean v3, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    if-nez v3, :cond_32

    .line 194
    const/16 v3, 0x8

    invoke-virtual {v0, v3}, Landroid/widget/LinearLayout;->setVisibility(I)V

    .line 196
    :cond_32
    iput-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->rightToolbar:Landroid/view/View;

    .line 197
    return-object v0
.end method

.method private createStateButton(Landroid/app/Activity;Lcom/eltechs/axs/KeyCodesX;Ljava/lang/String;)Lcom/eltechs/axs/StateButton;
    .registers 11
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "keyCodesX"    # Lcom/eltechs/axs/KeyCodesX;
    .param p3, "str"    # Ljava/lang/String;

    .prologue
    const/4 v3, 0x1

    .line 120
    new-instance v0, Lcom/eltechs/axs/StateButton;

    new-instance v2, Lcom/eltechs/axs/ButtonEventReporter;

    iget-object v1, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->mXServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

    new-array v4, v3, [Lcom/eltechs/axs/KeyCodesX;

    const/4 v5, 0x0

    aput-object p2, v4, v5

    invoke-direct {v2, v1, v4}, Lcom/eltechs/axs/ButtonEventReporter;-><init>(Lcom/eltechs/axs/xserver/ViewFacade;[Lcom/eltechs/axs/KeyCodesX;)V

    iget v4, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    iget v5, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonHeight:I

    move-object v1, p1

    move-object v6, p3

    invoke-direct/range {v0 .. v6}, Lcom/eltechs/axs/StateButton;-><init>(Landroid/content/Context;Lcom/eltechs/axs/ButtonEventReporter;ZIILjava/lang/String;)V

    return-object v0
.end method


# virtual methods
.method public attach(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)Landroid/view/View;
    .registers 15
    .param p1, "xServerDisplayActivity"    # Lcom/eltechs/axs/activities/XServerDisplayActivity;
    .param p2, "viewOfXServer"    # Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

    .prologue
    .line 56
    new-instance v7, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    iget-object v8, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->controlsFactory:Lcom/eltechs/axs/TouchScreenControlsFactory;

    sget-object v9, Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;->DEFAULT:Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;

    invoke-direct {v7, p1, p2, v8, v9}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;-><init>(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;Lcom/eltechs/axs/TouchScreenControlsFactory;Lcom/eltechs/axs/configuration/TouchScreenControlsInputConfiguration;)V

    iput-object v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    .line 57
    iget-object v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    const/4 v8, 0x1

    invoke-virtual {v7, v8}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;->setZOrderMediaOverlay(Z)V

    .line 58
    iput-object p2, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->mViewOfXServer:Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

    .line 59
    invoke-virtual {p2}, Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;->getXServerFacade()Lcom/eltechs/axs/xserver/ViewFacade;

    move-result-object v7

    iput-object v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->mXServerFacade:Lcom/eltechs/axs/xserver/ViewFacade;

    .line 60
    invoke-static {}, Lcom/eltechs/axs/helpers/AndroidHelpers;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    move-result-object v0

    .line 61
    .local v0, "displayMetrics":Landroid/util/DisplayMetrics;
    iget v7, v0, Landroid/util/DisplayMetrics;->widthPixels:I

    int-to-float v7, v7

    iget v8, v0, Landroid/util/DisplayMetrics;->densityDpi:I

    int-to-float v8, v8

    div-float/2addr v7, v8

    const/high16 v8, 0x40a00000    # 5.0f

    cmpl-float v7, v7, v8

    if-lez v7, :cond_f7

    const/4 v7, 0x1

    :goto_2b
    if-gez v7, :cond_10a

    const v7, 0x3ecccccd    # 0.4f

    :goto_30
    iget v8, v0, Landroid/util/DisplayMetrics;->densityDpi:I

    int-to-float v8, v8

    mul-float/2addr v7, v8

    float-to-int v7, v7

    iput v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    .line 62
    iget v7, v0, Landroid/util/DisplayMetrics;->heightPixels:I

    div-int/lit8 v7, v7, 0x8

    iput v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonHeight:I

    .line 63
    iget v3, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonHeight:I

    .line 64
    .local v3, "i":I
    iget v4, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    .line 65
    .local v4, "i2":I
    if-le v3, v4, :cond_44

    .line 66
    move v3, v4

    .line 68
    :cond_44
    iput v3, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonHeight:I

    .line 69
    new-instance v2, Landroid/widget/FrameLayout;

    invoke-direct {v2, p1}, Landroid/widget/FrameLayout;-><init>(Landroid/content/Context;)V

    .line 70
    .local v2, "frameLayout":Landroid/widget/FrameLayout;
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    const/4 v8, -0x1

    const/4 v9, -0x1

    invoke-direct {v7, v8, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    invoke-virtual {v2, v7}, Landroid/widget/FrameLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 71
    new-instance v6, Landroid/widget/LinearLayout;

    invoke-direct {v6, p1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    .line 72
    .local v6, "linearLayout":Landroid/widget/LinearLayout;
    const/4 v7, 0x0

    invoke-virtual {v6, v7}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 73
    new-instance v7, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v8, -0x1

    const/4 v9, -0x1

    invoke-direct {v7, v8, v9}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    invoke-virtual {v6, v7}, Landroid/widget/LinearLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 74
    invoke-direct {p0, p1, p2}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createLeftToolbar(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)Landroid/view/View;

    move-result-object v7

    invoke-virtual {v6, v7}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 75
    iget-object v7, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    new-instance v8, Landroid/widget/LinearLayout$LayoutParams;

    const/4 v9, 0x0

    const/4 v10, -0x1

    const/high16 v11, 0x3f800000    # 1.0f

    invoke-direct {v8, v9, v10, v11}, Landroid/widget/LinearLayout$LayoutParams;-><init>(IIF)V

    invoke-virtual {v6, v7, v8}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    .line 76
    invoke-direct {p0, p1, p2}, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->createRightToolbar(Lcom/eltechs/axs/activities/XServerDisplayActivity;Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)Landroid/view/View;

    move-result-object v7

    invoke-virtual {v6, v7}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    .line 77
    new-instance v7, Lcom/eltechs/axs/CommonApplicationConfigurationAccessor;

    invoke-direct {v7}, Lcom/eltechs/axs/CommonApplicationConfigurationAccessor;-><init>()V

    invoke-virtual {v7}, Lcom/eltechs/axs/CommonApplicationConfigurationAccessor;->isHorizontalStretchEnabled()Z

    move-result v7

    invoke-virtual {p2, v7}, Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;->setHorizontalStretchEnabled(Z)V

    .line 78
    const/4 v7, 0x5

    new-array v7, v7, [Lcom/eltechs/axs/widgets/actions/AbstractAction;

    const/4 v8, 0x0

    new-instance v9, Lcom/eltechs/axs/activities/menus/ShowKeyboard;

    invoke-direct {v9}, Lcom/eltechs/axs/activities/menus/ShowKeyboard;-><init>()V

    aput-object v9, v7, v8

    const/4 v8, 0x1

    new-instance v9, Lcom/eltechs/axs/activities/menus/ToggleHorizontalStretch;

    invoke-direct {v9}, Lcom/eltechs/axs/activities/menus/ToggleHorizontalStretch;-><init>()V

    aput-object v9, v7, v8

    const/4 v8, 0x2

    new-instance v9, Lcom/eltechs/axs/activities/menus/ToggleUiOverlaySidePanels;

    invoke-direct {v9}, Lcom/eltechs/axs/activities/menus/ToggleUiOverlaySidePanels;-><init>()V

    aput-object v9, v7, v8

    const/4 v8, 0x3

    new-instance v9, Lcom/eltechs/axs/activities/menus/ShowUsage;

    invoke-direct {v9}, Lcom/eltechs/axs/activities/menus/ShowUsage;-><init>()V

    aput-object v9, v7, v8

    const/4 v8, 0x4

    new-instance v9, Lcom/eltechs/axs/activities/menus/Quit;

    invoke-direct {v9}, Lcom/eltechs/axs/activities/menus/Quit;-><init>()V

    aput-object v9, v7, v8

    invoke-static {v7}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v7

    invoke-virtual {p1, v7}, Lcom/eltechs/axs/activities/XServerDisplayActivity;->addDefaultPopupMenu(Ljava/util/List;)V

    .line 79
    invoke-virtual {p2}, Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;->getConfiguration()Lcom/eltechs/axs/configuration/XServerViewConfiguration;

    move-result-object v7

    const/4 v8, 0x1

    invoke-virtual {v7, v8}, Lcom/eltechs/axs/configuration/XServerViewConfiguration;->setShowCursor(Z)V

    .line 80
    invoke-virtual {v2, v6}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;)V

    .line 81
    new-instance v1, Lcom/eltechs/axs/FloatButton;

    new-instance v7, Lcom/eltechs/axs/MouseButtonEventReporter;

    new-instance v8, Lcom/eltechs/axs/PointerEventReporter;

    iget-object v9, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->mViewOfXServer:Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;

    invoke-direct {v8, v9}, Lcom/eltechs/axs/PointerEventReporter;-><init>(Lcom/eltechs/axs/widgets/viewOfXServer/ViewOfXServer;)V

    const/4 v9, 0x3

    invoke-direct {v7, v8, v9}, Lcom/eltechs/axs/MouseButtonEventReporter;-><init>(Lcom/eltechs/axs/PointerEventReporter;I)V

    invoke-direct {v1, p1, v7}, Lcom/eltechs/axs/FloatButton;-><init>(Landroid/content/Context;Lcom/eltechs/axs/ButtonEventListener;)V

    .line 82
    .local v1, "floatButton":Lcom/eltechs/axs/FloatButton;
    iget v5, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    .line 83
    .local v5, "i3":I
    new-instance v7, Landroid/widget/LinearLayout$LayoutParams;

    invoke-direct {v7, v5, v5}, Landroid/widget/LinearLayout$LayoutParams;-><init>(II)V

    invoke-virtual {v1, v7}, Lcom/eltechs/axs/FloatButton;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 84
    const/high16 v7, 0x43960000    # 300.0f

    invoke-virtual {v1, v7}, Lcom/eltechs/axs/FloatButton;->setX(F)V

    .line 85
    const/high16 v7, 0x43960000    # 300.0f

    invoke-virtual {v1, v7}, Lcom/eltechs/axs/FloatButton;->setY(F)V

    .line 86
    invoke-virtual {v2, v1}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;)V

    .line 87
    return-object v2

    .line 61
    .end local v1    # "floatButton":Lcom/eltechs/axs/FloatButton;
    .end local v2    # "frameLayout":Landroid/widget/FrameLayout;
    .end local v3    # "i":I
    .end local v4    # "i2":I
    .end local v5    # "i3":I
    .end local v6    # "linearLayout":Landroid/widget/LinearLayout;
    :cond_f7
    iget v7, v0, Landroid/util/DisplayMetrics;->widthPixels:I

    int-to-float v7, v7

    iget v8, v0, Landroid/util/DisplayMetrics;->densityDpi:I

    int-to-float v8, v8

    div-float/2addr v7, v8

    const/high16 v8, 0x40a00000    # 5.0f

    cmpl-float v7, v7, v8

    if-nez v7, :cond_107

    const/4 v7, 0x0

    goto/16 :goto_2b

    :cond_107
    const/4 v7, -0x1

    goto/16 :goto_2b

    :cond_10a
    const v7, 0x3ee66666    # 0.45f

    goto/16 :goto_30
.end method

.method createScrollView(Landroid/app/Activity;)Landroid/widget/LinearLayout;
    .registers 6
    .param p1, "activity"    # Landroid/app/Activity;

    .prologue
    .line 124
    new-instance v0, Landroid/widget/LinearLayout;

    invoke-direct {v0, p1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    .line 125
    .local v0, "linearLayout":Landroid/widget/LinearLayout;
    new-instance v1, Landroid/view/ViewGroup$LayoutParams;

    iget v2, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->buttonWidth:I

    const/4 v3, -0x1

    invoke-direct {v1, v2, v3}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    .line 126
    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setOrientation(I)V

    .line 127
    const-string v1, "#292c33"

    invoke-static {v1}, Landroid/graphics/Color;->parseColor(Ljava/lang/String;)I

    move-result v1

    invoke-virtual {v0, v1}, Landroid/widget/LinearLayout;->setBackgroundColor(I)V

    .line 128
    return-object v0
.end method

.method public detach()V
    .registers 3

    .prologue
    const/4 v1, 0x0

    .line 92
    iget-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    invoke-virtual {v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;->detach()V

    .line 93
    iput-object v1, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->tscWidget:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsWidget;

    .line 94
    iput-object v1, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->leftToolbar:Landroid/view/View;

    .line 95
    iput-object v1, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->rightToolbar:Landroid/view/View;

    .line 96
    return-void
.end method

.method public isSidePanelsVisible()Z
    .registers 2

    .prologue
    .line 100
    iget-boolean v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    return v0
.end method

.method public toggleSidePanelsVisibility()V
    .registers 4

    .prologue
    const/16 v2, 0x8

    const/4 v1, 0x0

    .line 105
    iget-boolean v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    if-nez v0, :cond_19

    const/4 v0, 0x1

    :goto_8
    iput-boolean v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    .line 106
    iget-boolean v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->isToolbarsVisible:Z

    if-eqz v0, :cond_1b

    .line 107
    iget-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->leftToolbar:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 108
    iget-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->rightToolbar:Landroid/view/View;

    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    .line 113
    :goto_18
    return-void

    :cond_19
    move v0, v1

    .line 105
    goto :goto_8

    .line 111
    :cond_1b
    iget-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->leftToolbar:Landroid/view/View;

    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    .line 112
    iget-object v0, p0, Lcom/eltechs/axs/gamesControls/TouchPadInterfaceOverlay;->rightToolbar:Landroid/view/View;

    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    goto :goto_18
.end method
