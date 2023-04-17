package com.eltechs.ed.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.eltechs.axs.helpers.Assert;
import com.eltechs.ed.R;
import com.eltechs.ed.guestContainers.GuestContainer;
import com.eltechs.ed.guestContainers.GuestContainersManager;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnColAdapter;
import com.example.datainsert.exagear.FAB.dialogfragment.customcontrols.widgets.BtnKeyAdapter;
import com.example.datainsert.exagear.mutiWine.MutiWine;

import java.util.List;

/* loaded from: classes.dex */
public class ManageContainersFragment extends Fragment {
    private static final int CONT_ASYNC_ACTION_CLONE = 1;
    private static final int CONT_ASYNC_ACTION_DELETE = 2;
    private static final int CONT_ASYNC_ACTION_NEW = 0;
    private List<GuestContainer> mContainers;
    private TextView mEmptyTextView;
    private GuestContainersManager mGcm;
    private boolean mIsAsyncTaskRun;
    private OnManageContainersActionListener mListener;
    private ProgressDialog mProgressDialog;
    private String mProgressMessage;
    private RecyclerView mRecyclerView;

    @Override // android.support.v4.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnManageContainersActionListener) context;
        } catch (ClassCastException unused) {
            throw new ClassCastException(context.toString() + " must implement OnManageContainersActionListener");
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    //在这里创建右上角的菜单，需要先调用setHasOptionsMenu.才能进到这个函数里
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MutiWine.setOptionMenu(menu, this);
//        inflater.inflate(R.menu.ex_manage_containers_menu, menu);
    }


    /**
     * 准备添加到ex中的方法，用于从外部调用，执行创建容器task
     */
    public void callToCreateNewContainer() {
        new ContAsyncTask(0).execute();
    }

//    @Override // android.support.v4.app.Fragment
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        if (menuItem.getItemId() == R.id.manage_containers_new) {
//            new ContAsyncTask(0).execute();
//            return true;
//        }
//        return super.onOptionsItemSelected(menuItem);
//    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FrameLayout frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.ex_basic_list, viewGroup, false);
        this.mRecyclerView = (RecyclerView) frameLayout.findViewById(R.id.list);
        this.mEmptyTextView = (TextView) frameLayout.findViewById(R.id.empty_text);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(this.mRecyclerView.getContext(), 1));
        return frameLayout;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
        }
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mGcm = GuestContainersManager.getInstance(getContext());
        refreshContainersList();
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.wd_title_manage_containers);
        if (this.mIsAsyncTaskRun) {
            this.mProgressDialog = showProgressDialog(this.mProgressMessage);
        }
    }

    public void refreshContainersList() {
        this.mContainers = this.mGcm.getContainersList();
        this.mRecyclerView.setAdapter(new ContainersAdapter(this.mContainers));
        if (this.mContainers.isEmpty()) {
            this.mEmptyTextView.setVisibility(View.VISIBLE);
        }
    }

    public ProgressDialog showProgressDialog(String str) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(str);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public void closeProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    /* loaded from: classes.dex */
    public interface OnManageContainersActionListener {
        void onManageContainerSettingsClick(GuestContainer guestContainer);

        void onManageContainersInstallPackages(GuestContainer guestContainer);

        void onManageContainersRunExplorer(GuestContainer guestContainer);
    }

    /* loaded from: classes.dex */
    private class ContAsyncTask extends AsyncTask<GuestContainer, Void, Void> {
        private int mAction;

        public ContAsyncTask(int i) {
            this.mAction = i;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            ManageContainersFragment.this.mIsAsyncTaskRun = true;
            switch (this.mAction) {
                case 0:
                    ManageContainersFragment.this.mProgressMessage = "Creating container...";
                    break;
                case 1:
                    ManageContainersFragment.this.mProgressMessage = "Cloning container...";
                    break;
                case 2:
                    ManageContainersFragment.this.mProgressMessage = "Deleting container...";
                    break;
                default:
                    Assert.state(false);
                    break;
            }
            ManageContainersFragment.this.mProgressDialog = ManageContainersFragment.this.showProgressDialog(ManageContainersFragment.this.mProgressMessage);
        }

        @Override // android.os.AsyncTask
        public Void doInBackground(GuestContainer... guestContainerArr) {
            switch (this.mAction) {
                case 0:
                    ManageContainersFragment.this.mGcm.createContainer();
                    return null;
                case 1:
                    ManageContainersFragment.this.mGcm.cloneContainer(guestContainerArr[0]);
                    return null;
                case 2:
                    ManageContainersFragment.this.mGcm.deleteContainer(guestContainerArr[0]);
                    return null;
                default:
                    Assert.state(false);
                    return null;
            }
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            ManageContainersFragment.this.refreshContainersList();
            if (ManageContainersFragment.this.mProgressDialog != null) {
                ManageContainersFragment.this.closeProgressDialog(ManageContainersFragment.this.mProgressDialog);
            }
            ManageContainersFragment.this.mIsAsyncTaskRun = false;
        }
    }

    /* loaded from: classes.dex */
    private class ContainersAdapter extends RecyclerView.Adapter<ContainersAdapter.ViewHolder> {
        private final List<GuestContainer> mItems;

        public ContainersAdapter(List<GuestContainer> list) {
            this.mItems = list;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_basic_list_item_with_button, viewGroup, false));
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            viewHolder.mItem = this.mItems.get(i);
            viewHolder.mImage.setImageResource(R.drawable.ic_archive_24dp);
            viewHolder.mText.setText(viewHolder.mItem.mConfig.getName());
            if (ManageContainersFragment.this.mGcm.getCurrentContainer() != null && viewHolder.mItem == ManageContainersFragment.this.mGcm.getCurrentContainer()) {
                viewHolder.mView.setBackgroundResource(R.color.primary_light);
            }
            viewHolder.mButton.setOnClickListener(new View.OnClickListener() { // from class: com.eltechs.ed.fragments.ManageContainersFragment.ContainersAdapter.1


                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    final GuestContainer guestContainer = (GuestContainer) ContainersAdapter.this.mItems.get(viewHolder.getAdapterPosition());
                    PopupMenu popupMenu = new PopupMenu(ManageContainersFragment.this.getContext(), view);
                    popupMenu.inflate(R.menu.ex_container_popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() { // from class: com.eltechs.ed.fragments.ManageContainersFragment.ContainersAdapter.1.1


                        @Override // android.widget.PopupMenu.OnMenuItemClickListener
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.container_clone /* 2131296332 */:
                                    new ContAsyncTask(1).execute(guestContainer);
                                    break;
                                case R.id.container_delete /* 2131296333 */:
                                    new ContAsyncTask(2).execute(guestContainer);
                                    break;
                                case R.id.container_install_package /* 2131296334 */:
                                    ManageContainersFragment.this.mListener.onManageContainersInstallPackages(guestContainer);
                                    break;
                                case R.id.container_properties /* 2131296335 */:
                                    ManageContainersFragment.this.mListener.onManageContainerSettingsClick(guestContainer);
                                    break;
                                case R.id.container_run_explorer /* 2131296336 */:
                                    ManageContainersFragment.this.mListener.onManageContainersRunExplorer(guestContainer);
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() { // from class: com.eltechs.ed.fragments.ManageContainersFragment.ContainersAdapter.1.2


                        @Override // android.widget.PopupMenu.OnDismissListener
                        public void onDismiss(PopupMenu popupMenu2) {
                            ManageContainersFragment.this.refreshContainersList();
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public final int getItemCount() {
            return this.mItems.size();
        }

        /* loaded from: classes.dex */
        private class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public ImageButton mButton;
            public ImageView mImage;
            public GuestContainer mItem;
            public TextView mSubText;
            public TextView mText;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public ViewHolder(View view) {
                super(view);
                this.mView = view;
                this.mImage = (ImageView) view.findViewById(R.id.image);
                this.mText = (TextView) view.findViewById(R.id.text);
                this.mSubText = (TextView) view.findViewById(R.id.subtext);
                this.mButton = (ImageButton) view.findViewById(R.id.button);
                this.mText.setGravity(16);
                this.mSubText.setVisibility(View.GONE);
                this.mView.setClickable(false);
            }
        }
    }
}