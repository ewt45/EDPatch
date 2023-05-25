package com.eltechs.ed.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.ed.R;
import com.eltechs.ed.XDGLink;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.example.datainsert.exagear.shortcut.MoreShortcut;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ChooseXDGLinkFragment extends Fragment {
    public static final String ARG_IS_START_MENU = "IS_START_MENU";
    private static final String PARENT_DIR_NAME = "..";
    private static final int VIEW_TYPE_FOLDER = 1;
    private static final int VIEW_TYPE_LINK = 0;
    private List<GuestContainer> mContainers;
    private List<XDGNode> mCurrentItems;
    private XDGNode mCurrentNode;
    private int mDepth;
    private TextView mEmptyTextView;
    private GuestContainersManager mGcm;
    private boolean mIsStartMenu;
    private OnXDGLinkSelectedListener mListener;
    private RecyclerView mRecyclerView;

    /* loaded from: classes.dex */
    public interface OnXDGLinkSelectedListener {
        void onXDGLinkSelected(XDGLink xDGLink);
    }



    /* loaded from: classes.dex */
    public class XDGNode implements Comparable<XDGNode> {
        GuestContainer mCont;
        File mFile;
        XDGLink mLink;

        XDGNode(GuestContainer guestContainer, File file, XDGLink xDGLink) {
            this.mCont = guestContainer;
            this.mFile = file;
            this.mLink = xDGLink;
        }

        public boolean isUpNode() {
            return this.mFile.getPath().equals(ChooseXDGLinkFragment.PARENT_DIR_NAME);
        }

        @Override // java.lang.Comparable
        public int compareTo(@NonNull XDGNode xDGNode) {
            if (isUpNode()) {
                return -1;
            }
            if (!this.mFile.isDirectory() || xDGNode.mFile.isDirectory()) {
                if (this.mFile.isDirectory() || !xDGNode.mFile.isDirectory()) {
                    return this.mFile.compareTo(xDGNode.mFile);
                }
                return 1;
            }
            return -1;
        }

        @NonNull
        public String toString() {
            if (isUpNode()) {
                return ChooseXDGLinkFragment.PARENT_DIR_NAME;
            }
            if (this.mFile.isDirectory()) {
                return this.mFile.getName();
            }
            Assert.state(this.mLink.name != null);
            return this.mLink.name;
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnXDGLinkSelectedListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context + " must implement OnStartMenuLinkSelectedListener");
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.ex_basic_list, viewGroup, false);
        this.mRecyclerView = frameLayout.findViewById(R.id.list);
        this.mEmptyTextView = frameLayout.findViewById(R.id.empty_text);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this.mRecyclerView.getContext(), 1));
        return frameLayout;
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mGcm = GuestContainersManager.getInstance(getContext());
        this.mContainers = this.mGcm.getContainersList();
        this.mIsStartMenu = getArguments().getBoolean(ARG_IS_START_MENU);
        this.mDepth = 0;
        this.mCurrentNode = null;
        this.mCurrentItems = getCurrentNodeContent();
        if (this.mCurrentItems.isEmpty()) {
            this.mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            this.mRecyclerView.setAdapter(new XDGNodeAdapter(this.mCurrentItems));
        }
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(this.mIsStartMenu ? R.string.wd_title_start_menu : R.string.wd_title_desktop);
    }

    List<XDGNode> getRootNodeContent() {
        GuestContainer currentContainer = this.mGcm.getCurrentContainer();
        if (currentContainer != null) {
            return getNodeContent(new XDGNode(currentContainer, new File(this.mIsStartMenu ? currentContainer.mStartMenuPath : currentContainer.mDesktopPath), null), true);
        }
        ArrayList<XDGNode> arrayList = new ArrayList<>();
        for (GuestContainer guestContainer : this.mContainers) {
            arrayList.addAll(getNodeContent(new XDGNode(guestContainer, new File(this.mIsStartMenu ? guestContainer.mStartMenuPath : guestContainer.mDesktopPath), null), true));
        }
        return arrayList;
    }

    List<XDGNode> getNodeContent(XDGNode xDGNode, boolean z) {
        ArrayList<XDGNode> arrayList = new ArrayList<>();
        if (!z) {
            arrayList.add(new XDGNode(xDGNode.mCont, new File(PARENT_DIR_NAME), null));
        }
        File[] listFiles = xDGNode.mFile.listFiles();
        if (listFiles == null) {
            return arrayList;
        }
        for (File file : listFiles) {
            if (file.isDirectory()) {
                arrayList.add(new XDGNode(xDGNode.mCont, file, null));
            } else if (file.getName().toLowerCase().endsWith(".desktop")) {
                try {
                    arrayList.add(new XDGNode(xDGNode.mCont, file, new XDGLink(xDGNode.mCont, file)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrayList;
    }

    public List<XDGNode> getCurrentNodeContent() {
        List<XDGNode> nodeContent;
        if (this.mDepth == 0) {
            nodeContent = getRootNodeContent();
        } else {
            nodeContent = getNodeContent(this.mCurrentNode, false);
        }
        Collections.sort(nodeContent);
        return nodeContent;
    }

    public void refresh() {
        if (this.mCurrentNode != null && this.mCurrentNode.mFile.exists()) {
            this.mCurrentItems = getCurrentNodeContent();
        } else {
            this.mDepth = 0;
            this.mCurrentNode = null;
            this.mCurrentItems = getCurrentNodeContent();
        }
        if (this.mCurrentItems.isEmpty()) {
            this.mEmptyTextView.setVisibility(View.VISIBLE);
        }
        this.mRecyclerView.setAdapter(new XDGNodeAdapter(this.mCurrentItems));
    }

    /* loaded from: classes.dex */
    private class XDGNodeAdapter extends RecyclerView.Adapter<XDGNodeAdapter.ViewHolder> {
        private final List<XDGNode> mItems;

        public XDGNodeAdapter(List<XDGNode> list) {
            this.mItems = list;
        }

        @NonNull
        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_basic_list_item_with_button, viewGroup, false), i);
        }

        @SuppressLint("NonConstantResourceId")
        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            viewHolder.mItem = this.mItems.get(i);
            if (viewHolder.mItem.mLink == null) {
                viewHolder.mImage.setImageResource(R.drawable.ic_folder_open_24dp);
            } else {
                viewHolder.mImage.setImageDrawable(new BitmapDrawable(viewHolder.mView.getResources(), ChooseXDGLinkFragment.this.mGcm.getIconPath(viewHolder.mItem.mLink)));
                viewHolder.mImage.setScaleX(0.9f);
                viewHolder.mImage.setScaleY(0.9f);
            }
            viewHolder.mText.setText(viewHolder.mItem.toString());
            viewHolder.mSubText.setText(viewHolder.mItem.isUpNode() ? "" : viewHolder.mItem.mCont.mConfig.getName());
            viewHolder.mView.setOnClickListener(view -> {
                XDGNode xDGNode = mItems.get(viewHolder.getAdapterPosition());
                if (xDGNode.mLink != null) {
                    mListener.onXDGLinkSelected(xDGNode.mLink);
                    return;
                }
                if (xDGNode.isUpNode()) {
                    mDepth--;
                    mCurrentNode = new XDGNode(mCurrentNode.mCont, new File(mCurrentNode.mFile.getParent()), null);
                } else {
                    mDepth ++;
                    mCurrentNode = new XDGNode(xDGNode.mCont, xDGNode.mFile, null);
                }
                ChooseXDGLinkFragment.this.mCurrentItems = ChooseXDGLinkFragment.this.getCurrentNodeContent();
                ChooseXDGLinkFragment.this.mRecyclerView.setAdapter(new XDGNodeAdapter(ChooseXDGLinkFragment.this.mCurrentItems));
            });
            viewHolder.mButton.setOnClickListener(view -> {
                final XDGNode xDGNode = XDGNodeAdapter.this.mItems.get(viewHolder.getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(ChooseXDGLinkFragment.this.getContext(), view);
                popupMenu.inflate(ChooseXDGLinkFragment.this.mIsStartMenu ? R.menu.ex_startmenu_xdg_popup_menu : R.menu.ex_desktop_xdg_popup_menu);
                final GuestContainer guestContainer = xDGNode.mCont;
                if (guestContainer != null && guestContainer.mConfig.getRunGuide() != null && !guestContainer.mConfig.getRunGuide().isEmpty()) {
                    popupMenu.getMenu().add("Show run guide").setOnMenuItemClickListener(menuItem -> {
                        ContainerRunGuideDFragment.createDialog(guestContainer, true).show(ChooseXDGLinkFragment.this.getActivity().getSupportFragmentManager(), "CONT_RUN_GUIDE");
                        return true;
                    });
                }
                // from class: com.eltechs.ed.fragments.ChooseXDGLinkFragment.XDGNodeAdapter.2.2
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.xdg_copy_to_desktop /* 2131296542 */:
                            ChooseXDGLinkFragment.this.mGcm.copyXDGLinkToDesktop(xDGNode.mLink);
                            return true;
                        case R.id.xdg_delete_shortcut /* 2131296543 */:
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setTitle("Shortcut deletion");
                            builder.setIcon(R.drawable.ic_warning_24dp);
                            builder.setMessage("This will only delete shortcut, not application or associated container.\n\nDelete shortcut?");
                            builder.setPositiveButton("Delete", (dialogInterface, i1) -> {
                                xDGNode.mLink.linkFile.delete();
                                ChooseXDGLinkFragment.this.refresh();
                                dialogInterface.dismiss();
                            });
                            builder.setNegativeButton("Cancel", (dialogInterface, i1) -> dialogInterface.dismiss());
                            builder.show();
                            return true;
                        default:
                            return true;
                    }
                });
                popupMenu.setOnDismissListener(popupMenu2 -> ChooseXDGLinkFragment.this.refresh());

                //插入diamante，用于创建app快捷方式
                MoreShortcut.addOptionsToMenu(mIsStartMenu,popupMenu,xDGNode.mLink);

                popupMenu.show();
            });
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return this.mItems.get(i).mLink == null ? VIEW_TYPE_FOLDER : VIEW_TYPE_LINK;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final int getItemCount() {
            return this.mItems.size();
        }

        /* loaded from: classes.dex */
        private class ViewHolder extends RecyclerView.ViewHolder {
            public ImageButton mButton;
            public ImageView mImage;
            public XDGNode mItem;
            public TextView mSubText;
            public TextView mText;
            public final View mView;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public ViewHolder(View view, int i) {
                super(view);
                this.mView = view;
                this.mImage = view.findViewById(R.id.image);
                this.mText = view.findViewById(R.id.text);
                this.mSubText = view.findViewById(R.id.subtext);
                this.mButton = view.findViewById(R.id.button);
                if (i == VIEW_TYPE_FOLDER) {
                    this.mButton.setVisibility(View.GONE);
                }
            }
        }
    }
}