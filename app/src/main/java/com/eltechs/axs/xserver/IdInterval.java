package com.eltechs.axs.xserver;

/* loaded from: classes.dex */
public class IdInterval implements Comparable<IdInterval> {
    private final int idBase;
    private final int idMask;

    public IdInterval(int i, int i2) {
        this.idBase = i;
        this.idMask = i2;
    }

    public int getIdBase() {
        return this.idBase;
    }

    public int getIdMask() {
        return this.idMask;
    }

    public boolean isInInterval(int i) {
        return (i | this.idMask) == (this.idBase | this.idMask);
    }

    @Override // java.lang.Comparable
    public int compareTo(IdInterval idInterval) {
        if (this.idBase < idInterval.idBase) {
            return -1;
        }
        return this.idBase > idInterval.idBase ? 1 : 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof IdInterval) {
            IdInterval idInterval = (IdInterval) obj;
            return this.idBase == idInterval.idBase && this.idMask == idInterval.idMask;
        }
        return false;
    }

    public int hashCode() {
        return this.idBase | this.idMask;
    }
}