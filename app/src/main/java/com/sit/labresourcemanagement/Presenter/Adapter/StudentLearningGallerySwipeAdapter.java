package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentVideoPlayerFragment;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.Student.StudentLearningGalleryContentModel;
import com.sit.labresourcemanagement.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class StudentLearningGallerySwipeAdapter extends PagerAdapter {
	private final static String[] imageExtList = {"jpg", "png", "webp", "svg", "jpeg", "bmp", "tiff"};

	List<StudentLearningGalleryContentModel> contentModelList;
	Context context;
	View view;
	LayoutInflater layoutInflater;
	String url = ApiRoutes.getBase_url();

	//Card components
	TextView tvPageNumber, tvTitle, tvInstruction;
	ImageView ivPicture;
	ProgressBar progressBar;

	AlertDialog dialog;

	private AsyncTask mMyTask;
	private FragmentManager fragmentManager;

	public StudentLearningGallerySwipeAdapter(List<StudentLearningGalleryContentModel> contentModelList, Context context, FragmentManager fragmentManager) {
		this.contentModelList = contentModelList;
		this.context = context;
		this.fragmentManager = fragmentManager;
	}

	@Override
	public int getCount() {
		return contentModelList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return (view == (LinearLayout)object);
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = layoutInflater.inflate(R.layout.swipe_learning_gallery, container, false);

		//Initialize card components
		tvPageNumber = view.findViewById(R.id.textViewPageNumber);
		tvTitle = view.findViewById(R.id.textViewTitle);
		tvInstruction = view.findViewById(R.id.textViewInstruction);
		ivPicture = view.findViewById(R.id.imageViewInstructionImage);

		progressBar =(ProgressBar)view.findViewById(R.id.progress);

		//Setting the components
		StudentLearningGalleryContentModel contentModel = contentModelList.get(position);
		tvPageNumber.setText(contentModel.getPageNumber());
		tvTitle.setText(contentModel.getName());
		tvInstruction.setText(contentModel.getInstruction());

		//Set image
		if (contentModel.getImagePath().equals("null")){
			progressBar.setVisibility(View.GONE);
			Glide.with(context).clear(ivPicture);
			ivPicture.setImageResource(R.drawable.ic_image);
		} else {

			final String fullURL = url + contentModel.getImagePath();

			if(isUrlImage(fullURL).equals("gif")){
				Glide.with(context)
						.asGif()
						.load(fullURL)
						.listener(new RequestListener<GifDrawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
								ivPicture.setImageResource(R.drawable.ic_cross);
								progressBar.setVisibility(View.GONE);
								return false;
							}

							@Override
							public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
							}
						})
						.into(ivPicture);
			} else {
				Glide.with(context)
						.load(fullURL)
						.listener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								ivPicture.setImageResource(R.drawable.ic_cross);
								progressBar.setVisibility(View.GONE);
								return false;
							}

							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
							}
						})
						.into(ivPicture);
			}

			//ImageView onClick
			ivPicture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(dialog == null) {
						//Show loading
						loadingDialog();

						//Check if url is image or video
						if (isUrlImage(fullURL).equals("image")){
							downloadImage(fullURL);

						} else if (isUrlImage(fullURL).equals("video")){
							//assume video if not image
							Fragment fragment = new StudentVideoPlayerFragment(dialog);
							Bundle args = new Bundle();
							args.putString("URL", fullURL);
							fragment.setArguments(args);

							dialog = null;
							fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
						} else if (isUrlImage(fullURL).equals("gif")){
							dialog.dismiss();
							dialog = null;
						}
					}
				}
			});
		}

		container.addView(view);

		return view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}


	/*====================================================================================================================================================================*/

	private String isUrlImage (String urlString){
		String ext = urlString.substring(urlString.lastIndexOf('.') + 1, urlString.length()).toLowerCase();

		if (ext.equals("gif"))
			return "gif";

		for (int x = 0; x < imageExtList.length; x++){
			if (ext.equals(imageExtList[x])){
				return "image";
			}
		}

		return "video";
	}

	private void downloadImage (String stringUrl) {
		URL url = stringToUrl(stringUrl);
		if (url != null)
			mMyTask = new StudentLearningGallerySwipeAdapter.DownloadTask().execute(url);
	}

	private URL stringToUrl (String stringUrl){
		try{
			URL url = new URL(stringUrl);
			return url;
		}catch (MalformedURLException e){
			//e.printStackTrace();
		}
		return null;
	}

	private class DownloadTask extends AsyncTask <URL, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(URL... urls) {
			URL url = urls[0];
			HttpURLConnection connection = null;

			try{
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				InputStream inputStream = connection.getInputStream();
				BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

				Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

				return bmp;
			}catch(IOException e){
				//e.printStackTrace();
			}finally{
				connection.disconnect();
			}
			return null;
		}

		// When all async task done
		protected void onPostExecute(Bitmap result){
			if (result != null){
				popupImage(result);

			}else {
				Snackbar.make(view,"Error", Snackbar.LENGTH_LONG).show();
			}
		}
	}

	//Alert dialog for image popup
	private void popupImage (Bitmap image){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
		View mView = LayoutInflater.from(context).inflate(R.layout.custom_image_popup, null);

		PhotoView photoView = mView.findViewById(R.id.imageViewZoom);
		photoView.setImageBitmap(image);

		mBuilder.setView(mView);
		AlertDialog mDialog = mBuilder.create();
		mDialog.show();

		dialog.dismiss();
		dialog = null;
	}

	private void loadingDialog(){
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
		mBuilder.setMessage("Please wait while your media is loading...");
		mBuilder.setCancelable(false);

		dialog = mBuilder.create();
		dialog.show();
	}
}
