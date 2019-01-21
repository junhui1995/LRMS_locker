package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.R;

import java.io.IOException;

public class StudentVideoPlayerFragment extends Fragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

	/*This whole fragment was just a testing to play video. It works, but will need a lot of work.*/
	private View rootView;

	//For surfaceView
	private SurfaceView surfaceView;
	private MediaPlayer mediaPlayer;
	private MediaController mediaController;
	private Handler handler = new Handler();
	private SurfaceHolder surfaceHolder;
	private String mUrl;

	AlertDialog dialog;

	public StudentVideoPlayerFragment() {
		// Required empty public constructor
	}

	public StudentVideoPlayerFragment(AlertDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((MainActivity) getActivity()).disableDrawer();
		mUrl = getArguments().getString("URL");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_student_video_player, container, false);

		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(StudentVideoPlayerFragment.this);

		surfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mediaController != null){
					mediaController.show();
				}
				return false;
			}
		});

		mediaController = new MediaController(getContext());

		return rootView;
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDisplay(surfaceHolder);

		try{
			mediaPlayer.setDataSource(mUrl);
			mediaPlayer.prepare();
			mediaPlayer.setOnPreparedListener(StudentVideoPlayerFragment.this);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			mediaController = new MediaController(getContext());
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if(dialog != null)
			dialog.dismiss();

		mp.seekTo(0);
		mediaPlayer.start();

		mediaController.setMediaPlayer(this);
		mediaController.setAnchorView(surfaceView);
		handler.post(new Runnable() {

			public void run() {
				mediaController.setEnabled(true);
				mediaController.show();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		releaseMediaPlayer();
		((MainActivity) getActivity()).enableDrawer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		((MainActivity) getActivity()).enableDrawer();
	}

	private void releaseMediaPlayer() {
		if(mediaPlayer != null){
			mediaPlayer.release();
			mediaPlayer = null;
		}

	}

	@Override
	public void start() {
		mediaPlayer.start();
	}

	@Override
	public void pause() {
		mediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		mediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getAudioSessionId() {
		return mediaPlayer.getAudioSessionId();
	}
}
