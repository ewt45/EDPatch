package com.example.datainsert.exagear.controls.model.fileformat.v1;

import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ALPHA;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_BG_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_HEIGHT;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_ROUND_SHAPE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_TXT_COLOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN_WIDTH;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_BTN__TXT_SIZE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_CUSTOM_BTN_POS;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_MOVE_RELATIVE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_OFFWINDOW_DISTANCE;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_MOUSE_SENSITIVITY;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SHOW_CURSOR;
import static com.example.datainsert.exagear.controls.ControlsResolver.PREF_KEY_SIDEBAR_COLOR;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.eltechs.axs.Globals;
import com.eltechs.axs.helpers.Assert;
import com.example.datainsert.exagear.controls.ControlsResolver;
import com.example.datainsert.exagear.controls.model.FormatHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 标识名 Pref
 */
public class BlockPref implements FormatHelper.Block<Object> {

    @Override
    public Object stringToObj(List<String> lines) {
        int curLine = 0;
        String[] header = lines.get(curLine).substring(1).split(FormatHelper.propSeparator);
        Assert.isTrue(header[0].equals("Pref") && header[1].equals(getVersion()));
        curLine++;

        SharedPreferences.Editor editor = Globals.getAppContext().getSharedPreferences(ControlsResolver.PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE).edit();
        String[] keysValues = lines.get(curLine).split(FormatHelper.propSeparator);
        for (String kNVLine : keysValues) {
            String[] kv = kNVLine.split(FormatHelper.kvSeparator);
            switch (kv[0]) {
                case PREF_KEY_SHOW_CURSOR:
                    editor.putBoolean(PREF_KEY_SHOW_CURSOR, Boolean.parseBoolean(kv[1]));
                    break;
                case PREF_KEY_BTN_BG_COLOR:
                    editor.putInt(PREF_KEY_BTN_BG_COLOR, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_BTN_TXT_COLOR:
                    editor.putInt(PREF_KEY_BTN_TXT_COLOR, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_SIDEBAR_COLOR:
                    editor.putInt(PREF_KEY_SIDEBAR_COLOR, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_BTN_WIDTH:
                    editor.putInt(PREF_KEY_BTN_WIDTH, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_BTN_HEIGHT:
                    editor.putInt(PREF_KEY_BTN_HEIGHT, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_CUSTOM_BTN_POS:
                    editor.putBoolean(PREF_KEY_CUSTOM_BTN_POS, Boolean.parseBoolean(kv[1]));
                    break;
                case PREF_KEY_MOUSE_MOVE_RELATIVE:
                    editor.putBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, Boolean.parseBoolean(kv[1]));
                    break;
                case PREF_KEY_BTN_ALPHA:
                    editor.putInt(PREF_KEY_BTN_ALPHA, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_MOUSE_SENSITIVITY:
                    editor.putInt(PREF_KEY_MOUSE_SENSITIVITY, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_MOUSE_OFFWINDOW_DISTANCE:
                    editor.putInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_BTN__TXT_SIZE:
                    editor.putInt(PREF_KEY_BTN__TXT_SIZE, Integer.parseInt(kv[1]));
                    break;
                case PREF_KEY_BTN_ROUND_SHAPE:
                    editor.putBoolean(PREF_KEY_BTN_ROUND_SHAPE, Boolean.parseBoolean(kv[1]));
                    break;
            }
        }
        editor.apply();
        return null;
    }

    @Override
    public List<String> objToString(Object o) {

        SharedPreferences sp = Globals.getAppContext().getSharedPreferences(ControlsResolver.PREF_FILE_NAME_SETTING, Context.MODE_PRIVATE);
        List<String> lines = new ArrayList<>();
        String line = PREF_KEY_SHOW_CURSOR + FormatHelper.kvSeparator + sp.getBoolean(PREF_KEY_SHOW_CURSOR, true)
                + FormatHelper.propSeparator + PREF_KEY_BTN_BG_COLOR + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN_BG_COLOR, Color.WHITE)
                + FormatHelper.propSeparator + PREF_KEY_BTN_TXT_COLOR + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN_TXT_COLOR, Color.BLACK)
                + FormatHelper.propSeparator + PREF_KEY_SIDEBAR_COLOR + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_SIDEBAR_COLOR, Color.BLACK)
                + FormatHelper.propSeparator + PREF_KEY_BTN_WIDTH + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN_WIDTH, -2)
                + FormatHelper.propSeparator + PREF_KEY_BTN_HEIGHT + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN_HEIGHT, -2)
                + FormatHelper.propSeparator + PREF_KEY_CUSTOM_BTN_POS + FormatHelper.kvSeparator + sp.getBoolean(PREF_KEY_CUSTOM_BTN_POS, false)
                + FormatHelper.propSeparator + PREF_KEY_MOUSE_MOVE_RELATIVE + FormatHelper.kvSeparator + sp.getBoolean(PREF_KEY_MOUSE_MOVE_RELATIVE, false)
                + FormatHelper.propSeparator + PREF_KEY_BTN_ALPHA + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN_ALPHA, 255)
                + FormatHelper.propSeparator + PREF_KEY_MOUSE_SENSITIVITY + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_MOUSE_SENSITIVITY, 80)
                + FormatHelper.propSeparator + PREF_KEY_MOUSE_OFFWINDOW_DISTANCE + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_MOUSE_OFFWINDOW_DISTANCE, 0)
                + FormatHelper.propSeparator + PREF_KEY_BTN__TXT_SIZE + FormatHelper.kvSeparator + sp.getInt(PREF_KEY_BTN__TXT_SIZE, 4)
                + FormatHelper.propSeparator + PREF_KEY_BTN_ROUND_SHAPE + FormatHelper.kvSeparator + sp.getBoolean(PREF_KEY_BTN_ROUND_SHAPE, false);

        lines.add(line);
        lines.add(0, FormatHelper.blockPrefix + "Pref" + FormatHelper.propSeparator + getVersion() + FormatHelper.propSeparator + lines.size());
        return lines;
    }

    @Override
    public String getVersion() {
        return "1";
    }
}
