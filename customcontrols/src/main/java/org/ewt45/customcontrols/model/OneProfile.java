package org.ewt45.customcontrols.model;

import org.ewt45.customcontrols.toucharea.TouchArea;

import java.util.ArrayList;
import java.util.List;

public class OneProfile {
    int alpha=255;
    List<TouchArea> touchAreaList = new ArrayList<>();


    public List<TouchArea> getTouchAreaList() {
        return touchAreaList;
    }
}
