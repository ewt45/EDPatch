package com.eltechs.axs.xserver;

/* loaded from: classes.dex */
public final class Atom {
    private final int id;
    private final String name;

    public Atom(int i, String str) {
        this.id = i;
        this.name = str;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Atom) && this.id == ((Atom) obj).id;
    }

    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return String.format("[%d: %s]", Integer.valueOf(this.id), this.name);
    }
}