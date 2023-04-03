package com.eltechs.axs.xserver.impl.drawables.bitmapBacked;

import android.graphics.Bitmap;
import android.support.v4.view.MotionEventCompat;
import com.eltechs.axs.helpers.MathHelpers;
import com.eltechs.axs.xserver.Drawable;
import com.eltechs.axs.xserver.Window;
import com.eltechs.axs.xserver.impl.SmallIdsGenerator;
import com.eltechs.axs.xserver.impl.drawables.DrawablesFactoryImplBase;
import com.eltechs.axs.xserver.impl.drawables.ImageFormat;
import com.eltechs.axs.xserver.impl.drawables.Visual;
import java.util.ArrayList;
import java.util.Collection;

/* loaded from: classes.dex */
public class BitmapBackedDrawablesFactory extends DrawablesFactoryImplBase {
    private static final Collection<ImageFormat> supportedImageFormats;
    private final boolean imagesMustBePowerOfTwo;
    private static final Visual preferredVisual = Visual.makeDisplayableVisual(SmallIdsGenerator.generateId(), 24, 24, 16711680, MotionEventCompat.ACTION_POINTER_INDEX_MASK, 255);
    private static final Collection<Visual> supportedVisuals = new ArrayList();

    static {
        supportedVisuals.add(preferredVisual);
        supportedVisuals.add(Visual.makeNonDisplayableVisual(SmallIdsGenerator.generateId(), 1));
        supportedImageFormats = new ArrayList();
        supportedImageFormats.add(new ImageFormat(1, 1, 32));
        supportedImageFormats.add(new ImageFormat(24, 32, 32));
        supportedImageFormats.add(new ImageFormat(32, 32, 32));
    }

    public BitmapBackedDrawablesFactory(boolean z) {
        super(supportedVisuals, supportedImageFormats, preferredVisual);
        this.imagesMustBePowerOfTwo = z;
    }

    @Override // com.eltechs.axs.xserver.impl.drawables.DrawablesFactory
    public Drawable create(int i, Window window, int i2, int i3, Visual visual) {
        int i4;
        int i5;
        if (this.imagesMustBePowerOfTwo) {
            i4 = MathHelpers.upperPOT(i2);
            i5 = MathHelpers.upperPOT(i3);
        } else {
            i4 = i2;
            i5 = i3;
        }
        return new BitmapBackedDrawable(i, window, Bitmap.createBitmap(i4, i5, Bitmap.Config.ARGB_8888), i2, i3, visual);
    }
}
