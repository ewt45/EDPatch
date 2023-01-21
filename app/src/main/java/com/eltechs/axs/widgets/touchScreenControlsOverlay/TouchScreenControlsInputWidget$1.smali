.class Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;
.super Ljava/lang/Object;
.source "TouchScreenControlsInputWidget.java"

# interfaces
.implements Landroid/view/View$OnKeyListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->installKeyListener()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;


# direct methods
.method constructor <init>(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)V
    .registers 2
    .param p1, "this$0"    # Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    .prologue
    .line 85
    iput-object p1, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;->this$0:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onKey(Landroid/view/View;ILandroid/view/KeyEvent;)Z
    .registers 9
    .param p1, "v"    # Landroid/view/View;
    .param p2, "i"    # I
    .param p3, "keyEvent"    # Landroid/view/KeyEvent;

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 89
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getSource()I

    move-result v2

    const/16 v3, 0x2002

    if-ne v2, v3, :cond_1b

    .line 90
    const/4 v1, 0x4

    if-ne p2, v1, :cond_13

    .line 91
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v1

    if-nez v1, :cond_14

    .line 148
    :cond_13
    :goto_13
    return v0

    .line 93
    :cond_14
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v1

    if-ne v1, v0, :cond_13

    goto :goto_13

    .line 98
    :cond_1b
    const/16 v2, 0x52

    if-ne p2, v2, :cond_25

    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v2

    if-eq v2, v0, :cond_aa

    .line 105
    :cond_25
    const/16 v2, 0x17

    if-eq p2, v2, :cond_13

    .line 112
    const/16 v2, 0x66

    if-eq p2, v2, :cond_13

    const/16 v2, 0x68

    if-eq p2, v2, :cond_13

    .line 119
    const/16 v2, 0x67

    if-eq p2, v2, :cond_13

    const/16 v2, 0x69

    if-eq p2, v2, :cond_13

    .line 126
    if-eqz p2, :cond_77

    .line 127
    const-string v2, "TouchScr16Widget"

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "onKey: \u8f93\u5165\u4e86\u8ba4\u8bc6\u7684keycode\uff1a"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 128
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v2

    if-nez v2, :cond_64

    .line 129
    iget-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;->this$0:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    invoke-static {v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->access$000(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)Lcom/eltechs/axs/Keyboard;

    move-result-object v0

    invoke-virtual {v0, p2, p3}, Lcom/eltechs/axs/Keyboard;->handleKeyDown(ILandroid/view/KeyEvent;)Z

    move-result v0

    goto :goto_13

    .line 131
    :cond_64
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v2

    if-eq v2, v0, :cond_6c

    move v0, v1

    .line 132
    goto :goto_13

    .line 134
    :cond_6c
    iget-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;->this$0:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    invoke-static {v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->access$000(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)Lcom/eltechs/axs/Keyboard;

    move-result-object v0

    invoke-virtual {v0, p2, p3}, Lcom/eltechs/axs/Keyboard;->handleKeyUp(ILandroid/view/KeyEvent;)Z

    move-result v0

    goto :goto_13

    .line 135
    :cond_77
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    move-result v0

    const/4 v2, 0x2

    if-eq v0, v2, :cond_80

    move v0, v1

    .line 136
    goto :goto_13

    .line 140
    :cond_80
    const-string v0, "TouchScr16Widget"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "onKey: \u8f93\u5165\u4e0d\u8ba4\u8bc6\u7684keycode"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, "\uff0c\u4f5c\u4e3aunicode\u5904\u7406"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 141
    iget-object v0, p0, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget$1;->this$0:Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;

    invoke-static {v0}, Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;->access$000(Lcom/eltechs/axs/widgets/touchScreenControlsOverlay/TouchScreenControlsInputWidget;)Lcom/eltechs/axs/Keyboard;

    move-result-object v0

    invoke-virtual {v0, p3}, Lcom/eltechs/axs/Keyboard;->handleUnicodeKeyType(Landroid/view/KeyEvent;)Z

    move-result v0

    goto/16 :goto_13

    .line 145
    :cond_aa
    const-string v1, "TouchScr16Widget"

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "onKey: \u6700\u5916\u5c42\u76d1\u542c\u5230key\uff1a"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " ,\u5bf9\u5e94\u5b57\u7b26\uff1a"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {p3}, Landroid/view/KeyEvent;->getCharacters()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_13
.end method
