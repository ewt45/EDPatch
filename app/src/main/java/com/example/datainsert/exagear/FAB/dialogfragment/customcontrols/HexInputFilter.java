package com.example.datainsert.exagear.FAB.dialogfragment.customcontrols;

import android.text.InputFilter;
import android.text.Spanned;

public class HexInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c1 = source.charAt(i);
            if ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'f') || (c1 >= 'A' && c1 <= 'F'))
                builder.append(c1);
        }
        return builder.toString();//这个返回值是新的source啊，不是替换后的dest
    }
}
