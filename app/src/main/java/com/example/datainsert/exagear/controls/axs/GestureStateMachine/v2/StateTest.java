package com.example.datainsert.exagear.controls.axs.GestureStateMachine.v2;

import android.support.annotation.NonNull;

import com.eltechs.axs.GestureStateMachine.GestureContext;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateTest extends GState{


    public StateTest(GestureContext gestureContext, Map<String, Object> map) {
        super(gestureContext, map);
    }

    @NonNull
    public ParamsModel getRequiredData() {
        try {
            List<Class<? extends GState>> list = new ArrayList<>();
            list.get(0).getDeclaredConstructor(Map.class).newInstance(new HashMap<>());
            list.get(0).getDeclaredConstructor().newInstance();

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

    @Override
    public void notifyBecomeActive() {

    }

    @Override
    public void notifyBecomeInactive() {

    }
}
