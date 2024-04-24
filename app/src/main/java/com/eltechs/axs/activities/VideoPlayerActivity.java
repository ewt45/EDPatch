package com.eltechs.axs.activities;

import static com.eltechs.axs.activities.StartupActivity.RESULT_CODE_GOT_USER_INPUT;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.VideoView;
import com.eltechs.ed.R;
import com.eltechs.axs.applicationState.ApplicationStateBase;

/* loaded from: classes.dex */
public class VideoPlayerActivity extends FrameworkActivity<ApplicationStateBase<?>> {
    @Override // com.eltechs.axs.activities.AxsActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        setResult(RESULT_CODE_GOT_USER_INPUT);
        int intValue = getExtraParameter();
        setContentView(R.layout.video);
        VideoView videoView = findViewById(R.id.video_view_id);
        videoView.setOnCompletionListener(mediaPlayer -> finish());
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + intValue));
        videoView.requestFocus();
        videoView.start();
    }

    public void finishActivity(View view) {
        ((VideoView) findViewById(R.id.video_view_id)).stopPlayback();
        finish();
    }
}