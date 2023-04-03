package com.eltechs.axs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: classes.dex */
public class PointerMoveToKeyAdapter implements PointerEventListener {
    private static final int MOVE_ACC = 16;
    private static final int MOVE_DOWN = 4;
    private static final int MOVE_LEFT = 2;
    private static final int MOVE_NONE = 0;
    private static final int MOVE_RIGHT = 1;
    private static final int MOVE_UP = 8;
    private final float accelerateDelta;
    private Finger finger;
    private final KeyCodesX keyAccelerate;
    private final KeyEventReporter keyEventReporter;
    private final KeyCodesX[] keyMoveDown;
    private final KeyCodesX[] keyMoveLeft;
    private final KeyCodesX[] keyMoveRight;
    private final KeyCodesX[] keyMoveUp;
    private final boolean likeJoystick;
    private final float minimalCoordinateDelta;
    private final float minimalDelta;
    private Collection<KeyCodesX> activeKeyCode = new ArrayList<>();
    private int prevMovementMask = 0;

    public PointerMoveToKeyAdapter(float f, float f2, KeyCodesX[] keyCodesXArr, KeyCodesX[] keyCodesXArr2, KeyCodesX[] keyCodesXArr3, KeyCodesX[] keyCodesXArr4, KeyCodesX keyCodesX, boolean z, KeyEventReporter keyEventReporter) {
        this.keyMoveUp = keyCodesXArr == null ? new KeyCodesX[0] : keyCodesXArr;
        this.keyMoveDown = keyCodesXArr2 == null ? new KeyCodesX[0] : keyCodesXArr2;
        this.keyMoveLeft = keyCodesXArr3 == null ? new KeyCodesX[0] : keyCodesXArr3;
        this.keyMoveRight = keyCodesXArr4 == null ? new KeyCodesX[0] : keyCodesXArr4;
        this.keyAccelerate = keyCodesX;
        this.minimalDelta = f;
        this.accelerateDelta = f2;
        this.likeJoystick = z;
        this.keyEventReporter = keyEventReporter;
        this.minimalCoordinateDelta = f / 2.0f;
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerEntered(float f, float f2) {
        this.finger = new Finger(f, f2);
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerExited(float f, float f2) {
        this.finger = null;
        reportKeysReleased();
    }

    @Override // com.eltechs.axs.PointerEventListener
    public void pointerMove(float f, float f2) {
        float x = this.finger.getX() - f;
        float y = this.finger.getY() - f2;
        if (((float) Math.sqrt((x * x) + (y * y))) < this.minimalDelta) {
            if (this.likeJoystick) {
                reportKeysReleased();
                return;
            }
            return;
        }
        reportMoveDelta(x, y);
        if (this.likeJoystick) {
            return;
        }
        this.finger.update(f, f2);
    }

    private void reportMoveDelta(float f, float f2) {
        float f3 = this.minimalCoordinateDelta;
        if (Math.abs(f) > f3) {
            f3 = Math.abs(f);
        }
        if (Math.abs(f2) > f3) {
            f3 = Math.abs(f2);
        }
        int i = 0;
        ArrayList<KeyCodesX> arrayList = new ArrayList<>();
        if (Math.abs(f) > this.accelerateDelta || Math.abs(f2) > this.accelerateDelta) {
            arrayList.add(this.keyAccelerate);
            i = MOVE_ACC;
        }
        float f4 = f3 / 2.0f;
        if (Math.abs(f) >= f4) {
            if (f < 0.0f) {
                arrayList.addAll(Arrays.asList(this.keyMoveRight));
                i |= MOVE_RIGHT;
            } else if (f > 0.0f) {
                arrayList.addAll(Arrays.asList(this.keyMoveLeft));
                i |= MOVE_LEFT;
            }
        }
        if (Math.abs(f2) >= f4) {
            if (f2 < 0.0f) {
                arrayList.addAll(Arrays.asList(this.keyMoveDown));
                i |= MOVE_DOWN;
            } else if (f2 > 0.0f) {
                arrayList.addAll(Arrays.asList(this.keyMoveUp));
                i |= MOVE_UP;
            }
        }
        if (this.prevMovementMask != i) {
            reportKeysReleased();
            this.activeKeyCode = arrayList;
            this.prevMovementMask = i;
            if (i != MOVE_NONE) {
                reportKeysPressed();
            }
        }
    }

    private void reportKeysReleased() {
        if (this.prevMovementMask == 0) {
            return;
        }
        Iterator<KeyCodesX> it = this.activeKeyCode.iterator();
        while (it.hasNext()) {
            this.keyEventReporter.reportKeysRelease(it.next());
        }
        this.prevMovementMask = 0;
    }

    private void reportKeysPressed() {
        Iterator<KeyCodesX> it = this.activeKeyCode.iterator();
        while (it.hasNext()) {
            this.keyEventReporter.reportKeysPress(it.next());
        }
    }
}