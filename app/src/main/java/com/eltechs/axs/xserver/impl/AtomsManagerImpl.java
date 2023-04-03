package com.eltechs.axs.xserver.impl;

import com.eltechs.axs.xserver.Atom;
import com.eltechs.axs.xserver.AtomsManager;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class AtomsManagerImpl implements AtomsManager {
    private final ArrayList<Atom> atoms = new ArrayList<>();
    private final Map<String, Integer> atomsIds = new TreeMap();

    public AtomsManagerImpl() {
        this.atoms.add(null);
    }

    @Override // com.eltechs.axs.xserver.AtomsManager
    public int internAtom(String str) {
        Integer num = this.atomsIds.get(str);
        if (num == null) {
            num = Integer.valueOf(this.atoms.size());
            this.atoms.add(new Atom(num.intValue(), str));
            this.atomsIds.put(str, num);
        }
        return num.intValue();
    }

    @Override // com.eltechs.axs.xserver.AtomsManager
    public int getAtomId(String str) {
        Integer num = this.atomsIds.get(str);
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    @Override // com.eltechs.axs.xserver.AtomsManager
    public boolean isAtomRegistered(String str) {
        return getAtomId(str) != 0;
    }

    @Override // com.eltechs.axs.xserver.AtomsManager
    public Atom getAtom(int i) {
        if (i < this.atoms.size()) {
            return this.atoms.get(i);
        }
        return null;
    }

    @Override // com.eltechs.axs.xserver.AtomsManager
    public Atom getAtom(String str) {
        return getAtom(getAtomId(str));
    }
}
