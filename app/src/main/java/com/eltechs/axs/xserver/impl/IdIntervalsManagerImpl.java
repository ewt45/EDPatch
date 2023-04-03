package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.xserver.IdInterval;
import com.eltechs.axs.xserver.IdIntervalsManager;
import java.util.Iterator;
import java.util.TreeSet;

/* loaded from: classes.dex */
public class IdIntervalsManagerImpl implements IdIntervalsManager {
    private static final int MINIMAL_ID_MASK_BITS = 18;
    private static final int ZERO_TOP_BITS = 3;
    private final TreeSet<IdInterval> freeIds = new TreeSet<>();

    public IdIntervalsManagerImpl(int i) {
        Assert.isTrue(i > 0);
        int numberOfLeadingZeros = 32 - Integer.numberOfLeadingZeros(i);
        numberOfLeadingZeros = Integer.bitCount(i) == 1 ? numberOfLeadingZeros - 1 : numberOfLeadingZeros;
        Assert.isTrue(numberOfLeadingZeros <= 11, String.format("The number of intervals is too big: %d.", Integer.valueOf(i)));
        int i2 = 29 - numberOfLeadingZeros;
        int i3 = (1 << i2) - 1;
        for (int i4 = 1; i4 < i; i4++) {
            this.freeIds.add(new IdInterval(i4 << i2, i3));
        }
    }

    @Override // com.eltechs.axs.xserver.IdIntervalsManager
    public IdInterval getInterval() {
        if (!this.freeIds.isEmpty()) {
            Iterator<IdInterval> it = this.freeIds.iterator();
            IdInterval next = it.next();
            it.remove();
            return next;
        }
        return null;
    }

    @Override // com.eltechs.axs.xserver.IdIntervalsManager
    public void freeInterval(IdInterval idInterval) {
        Assert.isTrue(this.freeIds.add(idInterval));
    }
}
