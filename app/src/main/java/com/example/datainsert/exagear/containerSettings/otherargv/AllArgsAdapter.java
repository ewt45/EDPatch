package com.example.datainsert.exagear.containerSettings.otherargv;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.datainsert.exagear.QH.getOneLineWithTitle;
import static com.example.datainsert.exagear.RR.getS;
import static com.example.datainsert.exagear.RR.dimen.margin8Dp;
import static com.example.datainsert.exagear.RR.getSArr;
import static com.example.datainsert.exagear.containerSettings.otherargv.AdapterHelper.addCpuCoresPart2;
import static com.example.datainsert.exagear.containerSettings.otherargv.AdapterHelper.getOneItemView;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_EARLIER;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_ENV;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_FRONT;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.POS_LATER;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.TYPE_CMD;
import static com.example.datainsert.exagear.containerSettings.otherargv.Argument.TYPE_ENV;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.datainsert.exagear.QH;
import com.example.datainsert.exagear.RR;

import java.util.List;

public class AllArgsAdapter extends RecyclerView.Adapter<AllArgsAdapter.ViewHolder> {
    private static final String TAG = "AllArgsAdapter";
    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_TASKSET = 2;   //第一个固定显示cpu核心
    private static final int TYPE_MULTI = 3;      //多个同名但不同值的参数
    private final int contId;

    /**
     * 直接用all，修改直接同步吧
     */
    private final List<Argument> mData;
    private final String[][] typeChoices = new String[][]{
            getSArr(RR.othArg_edit_typeChoices),
            {TYPE_ENV + " " + POS_ENV, TYPE_CMD + " " + POS_FRONT, TYPE_CMD + " " + POS_EARLIER, TYPE_CMD + " " + POS_LATER}};

    public AllArgsAdapter(int contId) {
        this.contId = contId;
        mData = Arguments.all;
    }

    public List<Argument> getALlData() {
        return mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        return new ViewHolder(getOneItemView(viewGroup, false));
    }


    /**
     * Note that unlike ListView, RecyclerView will not call this method again
     * if the position of the item changes in the data set unless the item itself is invalidated
     * or the new position cannot be determined. For this reason, you should only use the
     * position parameter while acquiring the related data item inside this method
     * and should not keep a copy of it. If you need the position of an item later on (e.g. in a click listener),
     * use getAdapterPosition() which will have the updated adapter position.
     * Override onBindViewHolder(ViewHolder, int, List) instead if Adapter can handle efficient partial bind.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Argument argument = mData.get(position);
        boolean isTypeCPU = holder.getItemViewType() == TYPE_TASKSET;
        boolean isTypeMulti = holder.getItemViewType() == TYPE_MULTI;

        holder.ck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (holder.getItemViewType() == TYPE_NORMAL)
                mData.get(holder.getAdapterPosition()).setChecked(isChecked);

                //cpu核心，每次点击，实时更新存储。如果是勾选，底部显示每个核心的勾选框
            else if (holder.getItemViewType() == TYPE_TASKSET) {
                if (!isChecked)
                    mData.get(holder.getAdapterPosition()).setArg("");

                //显示或隐藏核心数
                TransitionManager.beginDelayedTransition(holder.root);
                ViewGroup.LayoutParams params = holder.linearPart2.getLayoutParams();
                params.height = isChecked ? -2 : 0;
                holder.linearPart2.setLayoutParams(params);
            }
        });
        if (!isTypeMulti)
            holder.ck.setChecked(argument.isChecked());
        else {
            holder.ck.setText("▶");
            holder.ck.setRotation(0);
            holder.ck.setButtonDrawable(null);
            holder.ck.setOnClickListener(v -> {
                boolean isFolded = holder.ck.getRotation() == 0;
                TransitionManager.beginDelayedTransition(holder.root);
                holder.ck.animate().rotation(isFolded ? 90 : 0).setDuration(300).start();
                ViewGroup.LayoutParams params = holder.linearPart2.getLayoutParams();
                params.height = isFolded ? -2 : 0;
                holder.linearPart2.setLayoutParams(params);
            });
        }

        holder.tv.setText(argument.getAlias());
        QH.setTextViewExpandable(holder.tv);
//        holder.subTv.setVisibility(isTypeMulti ? VISIBLE : GONE);
        holder.subTv.setVisibility(GONE);
        if (isTypeMulti && argument.isChecked()) {
            holder.subTv.setText(Arguments.getRippedAlias(argument, argument.getCheckedSubParamsInGroup()));
        }

        holder.btnMenu.setVisibility((isTypeMulti || isTypeCPU) ? View.INVISIBLE : VISIBLE);
        holder.btnMenu.setOnClickListener(v -> buildItemActionMenu(holder, v, -1));

        //cpu核心，添加gridlayout
        if (isTypeCPU) {
            addCpuCoresPart2(holder, contId, argument);
        }

        //参数组，添加多个子选项
        else if (isTypeMulti) {
            holder.linearPart2.removeAllViews();
            for (int i = 0; i < argument.getGroup().size(); i++) {
                Argument subArg = argument.getGroup().get(i);
                LinearLayout linearOneItem = getOneItemView(holder.root, true);
                ((CheckBox) linearOneItem.findViewById(android.R.id.checkbox)).setOnCheckedChangeListener((buttonView, isChecked) -> {
                    subArg.setChecked(isChecked);
                    if (!isChecked)
                        return;
                    //如果勾选这个，则取消勾选其他的，其他的都取消勾选
                    for (int subItemInd = 0; subItemInd < holder.linearPart2.getChildCount(); subItemInd++) {
                        CheckBox otherCheck = ((CheckBox) holder.linearPart2.getChildAt(subItemInd).findViewById(android.R.id.checkbox));
                        if (otherCheck != buttonView)
                            otherCheck.setChecked(false);
                    }
                });
                ((CheckBox) linearOneItem.findViewById(android.R.id.checkbox)).setChecked(subArg.isChecked());
                ((TextView) linearOneItem.findViewById(android.R.id.text1)).setText(Arguments.getRippedAlias(argument, subArg));
                linearOneItem.findViewById(android.R.id.text2).setVisibility(GONE);
                int finalI = argument.getGroup().indexOf(subArg);
                linearOneItem.findViewById(android.R.id.button1).setOnClickListener(v -> buildItemActionMenu(holder, v, finalI));
                holder.linearPart2.addView(linearOneItem);
            }
        }

        //只有cpu核心且勾选了它的时候才显示第二布局，否则均隐藏（初次构建布局就没必要加动画了吧）
        holder.linearPart2.getLayoutParams().height = (isTypeCPU && holder.ck.isChecked()) ? -2 : 0;
        holder.linearPart2.setLayoutParams(holder.linearPart2.getLayoutParams());
    }

    /**
     * 点击item右侧的三个点时弹出的popupMenu
     *
     * @param subInd 若单参数，为-1。若参数组，为子参数在参数中的序号
     */
    private void buildItemActionMenu(ViewHolder holder, View v, int subInd) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenu().add(getS(RR.global_edit)).setOnMenuItemClickListener(item -> {  //编辑
            buildEditArgDialog(v.getContext(), holder.getAdapterPosition(), subInd);
            return true;
        });
        popupMenu.getMenu().add(getS(RR.global_del)).setOnMenuItemClickListener(item -> {  //删除
            int adapterPos = holder.getAdapterPosition();
            Argument argument = subInd == -1 ? mData.get(adapterPos) : mData.get(adapterPos).getGroup().get(subInd);
            new AlertDialog.Builder(v.getContext())
                    .setMessage(getS(RR.othArg_edit_delConfirm) + argument.getArg())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        if (subInd == -1) {
                            mData.remove(adapterPos);
                            notifyItemRemoved(adapterPos);
                        } else {
                            Arguments.removeArgFromGroup(mData, adapterPos, subInd);
                            notifyItemChanged(adapterPos);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create().show();
            return true;
        });
        popupMenu.show();
    }


    /**
     * 显示一个dialog，用于编辑一个参数的必要信息。可能为新建（oriPos=-1），也可能为修改现有（oriPos=其在mData中的位置）。
     * subInd不为-1，说明是参数组中的某个参数，否则表明这是个单参数
     */
    public void buildEditArgDialog(Context c, int oriPos, int subInd) {
        boolean isNew = oriPos == -1;
        Argument currArg;
        if (isNew) currArg = null;
        else if (subInd == -1) currArg = mData.get(oriPos);
        else currArg = mData.get(oriPos).getGroup().get(subInd);

        LinearLayout linearAddRoot = new LinearLayout(c);
        linearAddRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams marginTopParams = new LinearLayout.LayoutParams(-1, -2);
        marginTopParams.topMargin = margin8Dp();

        EditText editAlias = new EditText(c);
        editAlias.setSingleLine(true);
        editAlias.setText(isNew ? "" : currArg.getAlias());
        editAlias.addTextChangedListener((QH.SimpleTextWatcher) s -> {
            for (int i = 0; i < s.length(); i++)
                if (s.charAt(i) == ' ') {
                    s.replace(i, i + 1, "");
                    i--;
                }
        });

        EditText editArg = new EditText(c);
        editArg.setSingleLine(true);
        editArg.setText(isNew ? "" : currArg.getArg());

        TextView tvArgType = new TextView(c);
        tvArgType.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvArgType.setOnClickListener(vv -> {
            PopupMenu popupMenu = new PopupMenu(vv.getContext(), vv);
            for (int i = 0; i < typeChoices[0].length; i++)
                popupMenu.getMenu().add(0, i + 1, i + 1, typeChoices[0][i]);
            popupMenu.setOnMenuItemClickListener(item -> {
                int selTypeInd = item.getItemId() - 1;
                tvArgType.setText(item.getTitle());
                tvArgType.setTag(typeChoices[1][selTypeInd]);
                return true;
            });
            popupMenu.show();
        });

        //将种类的类型存到tag中。
        if (isNew) {
            tvArgType.setText(typeChoices[0][0]);
            tvArgType.setTag(typeChoices[1][0]);
        } else {
            String tag = currArg.getArgType() + " " + currArg.getArgPos();
            for (int i = 0; i < typeChoices[1].length; i++)
                if (tag.equals(typeChoices[1][i]))
                    tvArgType.setText(typeChoices[0][i]);
            tvArgType.setTag(tag);
        }

        String[] attrStrs = getSArr(RR.othArg_edit_attrTitles);
        CheckBox checkEnable = new CheckBox(c);
        checkEnable.setText(attrStrs[3]);//"新建的容器默认添加该参数"
        checkEnable.setChecked(isNew || currArg.isEnableByDefault());

        LinearLayout linearType = getOneLineWithTitle(c, attrStrs[2], tvArgType, false);
        TextView btnTypeInfo = QH.getInfoIconView(c, getS(RR.othArg_edit_typeInfo));
        linearType.addView(btnTypeInfo, 1);

        linearAddRoot.addView(getOneLineWithTitle(c, attrStrs[0], editAlias, false)); //参数别名
        linearAddRoot.addView(getOneLineWithTitle(c, attrStrs[1], editArg, false));   //参数内容
        linearAddRoot.addView(linearType, marginTopParams); //参数类型
        linearAddRoot.addView(checkEnable, marginTopParams);

        new AlertDialog.Builder(c)
                .setView(QH.wrapAsDialogScrollView(linearAddRoot))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    //点击确定时，生成新的Argument，放入mData列表，然后通知Adapter变化
                    String argContent = editArg.getText().toString();
                    String[] typeNPos = ((String) tvArgType.getTag()).split(" ", 2);

                    //参数内容为空，或参数类型为环境变量但内容中没有等号，则不生成新参数实例
                    if (argContent.trim().length() == 0 || (TYPE_ENV.equals(typeNPos[0]) && !argContent.trim().contains("=")))
                        return;

                    Argument newArg = Arguments.inflateArgument(currArg, checkEnable.isChecked() ? "e" : "d", typeNPos[0], typeNPos[1], editAlias.getText().toString(), argContent.trim());
                    if (!isNew) {
                        notifyItemChanged(oriPos);//Arguments.inflateArgument会直接修改现有arg对象，不需要移除再添加了
                        return;
                    }

                    int insertPos = Arguments.addArgAsGroup(mData, newArg);
                    if (insertPos != -1) {
                        notifyItemChanged(insertPos);
                    } else {
                        mData.add(1, newArg);//新参数，考虑是否可以加入参数组。如果是单参数就放到cpu核心之后（第二项）。
                        notifyItemInserted(1);
                    }
                })
                .setCancelable(false)
                .create().show();
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_TASKSET;
        else if (mData.get(position).isGroup())
            return TYPE_MULTI;
        else return TYPE_NORMAL;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        LinearLayout linearPart1;
        LinearLayout linearPart2;
        CheckBox ck;
        TextView tv;
        TextView subTv;
        ImageButton btnMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = (LinearLayout) itemView;
            linearPart1 = (LinearLayout) root.getChildAt(0);
            linearPart2 = (LinearLayout) root.getChildAt(1);
            ck = root.findViewById(android.R.id.checkbox);
            tv = root.findViewById(android.R.id.text1);
            subTv = root.findViewById(android.R.id.text2);
            btnMenu = root.findViewById(android.R.id.button1);

        }
    }
}
