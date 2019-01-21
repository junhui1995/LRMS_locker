package com.sit.labresourcemanagement.Presenter.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

public class StudentLearningGalleryContentAdapter extends RecyclerView.Adapter<StudentLearningGalleryContentAdapter.ViewHolder>{

	private final static String[] imageExtList = {"jpg", "png", "webp", "svg", "jpeg", "bmp", "tiff"};

	List<StudentLearningGalleryContentModel> contentModelList;
	Context context;
	View view;
	String url = ApiRoutes.getBase_url();

	private AsyncTask mMyTask;
	private FragmentManager fragmentManager;

	public StudentLearningGalleryContentAdapter(List<StudentLearningGalleryContentModel> contentModelList, Context context, FragmentManager fragmentManager) {
		this.contentModelList = contentModelList;
		this.context = context;
		this.fragmentManager = fragmentManager;
	}

	class ViewHolder extends RecyclerView.ViewHolder{
		TextView tvPageNumber, tvTitle, tvInstruction;
		ImageView ivPicture;

		public ViewHolder(final View itemView) {
			super(itemView);

			tvPageNumber = itemView.findViewById(R.id.textViewPageNumber);
			tvTitle = itemView.findViewById(R.id.textViewTitle);
			tvInstruction = itemView.findViewById(R.id.textViewInstruction);
			ivPicture = itemView.findViewById(R.id.imageViewInstructionImage);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_learning_gallery, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final StudentLearningGalleryContentModel contentModel = contentModelList.get(position);

		holder.tvPageNumber.setText(contentModel.getPageNumber());
		holder.tvTitle.setText(contentModel.getName());
		holder.tvInstruction.setText(contentModel.getInstruction());

		//Get the path of the image then load using glide.
		if (contentModel.getImagePath().equals("null")){
			holder.ivPicture.setImageResource(R.drawable.ic_image);
		} else {
			final String fullURL = url + contentModel.getImagePath();

			Glide.with(context)
					.load(fullURL)
					.into(holder.ivPicture);


			holder.ivPicture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//Check if url is image or video
					if (isUrlImage(fullURL).equals("image")) {
						downloadImage(fullURL);
					} else if (isUrlImage(fullURL).equals("video")) {
						//assume video if not image
						Fragment fragment = new StudentVideoPlayerFragment();
						Bundle args = new Bundle();
						args.putString("URL", fullURL);
						fragment.setArguments(args);

						fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
					} else if (isUrlImage(fullURL).equals("gif")) {
						Glide.with(context)
								.load(fullURL)
								.into(holder.ivPicture);
					}
				}
			});
		}

	}

	@Override
	public int getItemCount() {
		return contentModelList.size();
	}

	private void downloadImage (String stringUrl) {
		URL url = stringToUrl(stringUrl);
		if (url != null)
			mMyTask = new DownloadTask().execute(url);
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
	}


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



}
