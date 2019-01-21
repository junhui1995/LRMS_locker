package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.Model.User;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentLearningGalleryFragment extends Fragment{
	private static final String TAG = "Learning Gallery";
	private static final String SWIPE = "swipe";
	private static final String SCROLL = "scroll";

	private View rootview;
	private boolean hasTag = false;
	private String mSwipeScroll;
	private String mTag;

	ListView lvFolder;
	Button btnBack, btnRoot;

	String url = ApiRoutes.getUrl();
	User mUser;

	HashMap<String, String> folderIdNameMap;
	ArrayList<String> folderNameList = new ArrayList<>();
	ArrayAdapter folderAdapter;
	List<String> folderIdList;

    public StudentLearningGalleryFragment() {}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//A list of folder ids to trace the folders selected by user.
		//For tags searches, will also store in the list but add "searchTag_" to separate folder from tags.
		folderIdList = new ArrayList<>();
		folderIdList.add("root");

		mUser = SharedPrefManager.getInstance(getContext()).getUser();
		mSwipeScroll = mUser.getNav();

		Bundle bundle = getArguments();

		if(null!=bundle) {
			//Get the equipment tag from loan page
			mTag = bundle.getString("tag");
			hasTag = true;
		}

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	rootview = inflater.inflate(R.layout.fragment_student_learning_gallery, container, false);

    	lvFolder = (ListView) rootview.findViewById(R.id.listViewFolder);
    	lvFolder.setDivider(null);
		folderAdapter = new ArrayAdapter(getContext(), R.layout.card_learning_gallery_folder, R.id.textViewFolderName, folderNameList);
		lvFolder.setAdapter(folderAdapter);

    	lvFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String folderid = folderIdNameMap.get(folderNameList.get(position));
				folderIdList.add(folderid);
				loadFolders(folderid);
			}
		});

    	if(hasTag){
    		searchTag(mTag);
		} else {
			loadFolders(folderIdList.get(folderIdList.size() - 1));
		}

		btnBack = (Button) rootview.findViewById(R.id.button_back);
		btnRoot = (Button) rootview.findViewById(R.id.button_main);

		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String tempId = folderIdList.get(folderIdList.size()-1);
				if (!tempId.equals("root")){
					folderIdList.remove(folderIdList.size()-1);
					tempId = folderIdList.get(folderIdList.size()-1);
					Log.e("tempId", folderIdList.toString());
					if(tempId.contains("_")){
						if (tempId.substring(0, tempId.indexOf("_")).equals("searchTag")){
							folderIdList.remove(folderIdList.size()-1);
							searchTag(tempId.substring(tempId.indexOf("_") + 1, tempId.length()));
						} else {
							loadFolders(folderIdList.get(folderIdList.size()-1));
						}
					} else {
						loadFolders(folderIdList.get(folderIdList.size()-1));
					}
				} else {
					Snackbar.make(getView(), "Already at root", Snackbar.LENGTH_SHORT).show();
				}

			}
		});

		btnRoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				folderIdList.clear();
				folderIdList.add("root");
				loadFolders("root");
			}
		});

		return rootview;
	}

	private void searchTag (final String searchTag){

    	//Search for folders tagged with the search tag.
		final RequestQueue searchByTag_queue = Volley.newRequestQueue(getContext());

		StringRequest getFolderByTag_req = new StringRequest(Request.Method.POST, url + "searchTag.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					if (jsonObject.getString("status").equals("Success")){

						JSONArray jsonArray = jsonObject.getJSONArray("folderList");
						folderIdList.add("searchTag_" + searchTag);
						folderNameList.clear();
						folderAdapter.clear();
						folderIdNameMap = new HashMap<>();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);
							String folderid = jsonObject1.getString("fid");
							String foldername = jsonObject1.getString("name");

							folderIdNameMap.put(foldername, folderid);
							folderNameList.add(foldername);
						}
						folderAdapter.notifyDataSetChanged();

					} else if (jsonObject.getString("status").equals("No Record Found")){
						Snackbar.make(getView(), "Tag not found", Snackbar.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {

				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String>  params = new HashMap<String, String>();
				params.put("tag", searchTag);
				return params;
			}
		};
		searchByTag_queue.add(getFolderByTag_req);
	}

	private void loadFolders(final String folderID) {

		final RequestQueue learningGallery_queue = Volley.newRequestQueue(getContext());

		StringRequest getFolder_req = new StringRequest(Request.Method.POST, url + "getFolder.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")){
						JSONArray jsonArray = jsonObject.getJSONArray("folderList");
						folderNameList.clear();
						folderAdapter.clear();
						folderIdNameMap = new HashMap<>();

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject1 = jsonArray.getJSONObject(i);
							String folderid = jsonObject1.getString("fid");
							String foldername = jsonObject1.getString("name");

							folderIdNameMap.put(foldername, folderid);
							folderNameList.add(foldername);
						}
						folderAdapter.notifyDataSetChanged();
					} else if (jsonObject.getString("status").equals("No Record Found")){

						//if record not found, no need store in history
						String prevFolderId = folderIdList.get(folderIdList.size()-1);
						if (prevFolderId.equals(folderID) && !prevFolderId.equals("root"))
							folderIdList.remove(folderIdList.size()-1);

						//Check if folder contains pages
						checkFolderContents(folderID, learningGallery_queue);

					}
				} catch (JSONException e) {

				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String>  params = new HashMap<String, String>();
				params.put("folderID", folderID);
				return params;
			}
		};

		learningGallery_queue.add(getFolder_req);
	}

	private void checkFolderContents(final String folderID, RequestQueue learningGallery_queue){
		StringRequest checkFolderContent_req = new StringRequest(Request.Method.POST, url + "getFolderContent.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")){
						Fragment fragment;
						if (mSwipeScroll.equals(SCROLL)){
							fragment = new StudentLearningMaterialFragment();
						} else {
							fragment = new StudentLearningMaterialSwipeFragment();
						}

						Bundle args = new Bundle();
						args.putString("folderid", folderID);
						fragment.setArguments(args);

						getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(TAG).commit();

					} else if (jsonObject.getString("status").equals("No Record Found")){

						String prevFolderId = folderIdList.get(folderIdList.size()-1);
						if (prevFolderId.equals(folderID) && !prevFolderId.equals("root"))
							folderIdList.remove(folderIdList.size()-1);
						Snackbar.make(getView(), "Folder is empty!",Snackbar.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					//e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String>  params = new HashMap<String, String>();
				params.put("job", "check");
				params.put("folderID", folderID);
				return params;
			}
		};

		learningGallery_queue.add(checkFolderContent_req);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		super.onCreateOptionsMenu(menu, inflater);

		//Search bar
		final MenuItem search = menu.findItem(R.id.app_bar_search);
		search.setVisible(true);

		final SearchView searchView = (SearchView) search.getActionView();
		searchView.setQueryHint("Enter a tag");
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				//All tags are lower case
				//Once user has searched for a tag, just close it. Might need to test to see if user prefers auto close.
				searchTag(query.toLowerCase());
				searchView.onActionViewCollapsed();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		//Swipe or scroll
		//From our interviews, we found the ratio of navigation preference to be 7:3 in favour of swiping.
		//Implemented both to let them choose.
		final MenuItem navStyle = menu.findItem(R.id.app_bar_navStyle);
		navStyle.setVisible(true);

		if (mSwipeScroll.equals(SCROLL))
			navStyle.setIcon(R.drawable.ic_up_down_arrow);
		else
			navStyle.setIcon(R.drawable.ic_left_right_arrow);

		navStyle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if(mSwipeScroll.equals(SCROLL)){
					mSwipeScroll = SWIPE;
					navStyle.setIcon(R.drawable.ic_left_right_arrow);
				} else {
					mSwipeScroll = SCROLL;
					navStyle.setIcon(R.drawable.ic_up_down_arrow);
				}
				Snackbar.make(getView(), "Set view to " + mSwipeScroll, Snackbar.LENGTH_SHORT).show();
				return true;
			}
		});
	}



}
