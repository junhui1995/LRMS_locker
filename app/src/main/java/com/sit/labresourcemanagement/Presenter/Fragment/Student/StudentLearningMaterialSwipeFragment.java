package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sit.labresourcemanagement.Presenter.Activity.MainActivity;
import com.sit.labresourcemanagement.Presenter.Adapter.StudentLearningGallerySwipeAdapter;
import com.sit.labresourcemanagement.Model.ApiRoutes;
import com.sit.labresourcemanagement.Model.Student.StudentLearningGalleryContentModel;
import com.sit.labresourcemanagement.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentLearningMaterialSwipeFragment extends Fragment {

    private View rootview;

    String folderID;
    TextView tvTotalPages;
    EditText etJumpToPage;
    Button btnJump;

    ViewPager viewPager;
    StudentLearningGallerySwipeAdapter adapter;

	String url = ApiRoutes.getUrl();
	List<StudentLearningGalleryContentModel> contentModelList = new ArrayList<>();

    public StudentLearningMaterialSwipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderID = getArguments().getString("folderid");
		((MainActivity)getActivity()).disableDrawer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_student_swipe_manual, container, false);

        tvTotalPages = (TextView) rootview.findViewById(R.id.textViewPageLimit);
        etJumpToPage = (EditText) rootview.findViewById(R.id.editTextJumpPage);
        btnJump = (Button) rootview.findViewById(R.id.buttonJump);
        viewPager = (ViewPager) rootview.findViewById(R.id.viewPagerSwipe_LearningGallery);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				etJumpToPage.setText(String.valueOf(position + 1));
			}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});

        btnJump.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//Close the keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}

				if (etJumpToPage.getText().toString().isEmpty()){
					if(getView() != null)
						Snackbar.make(getView(), "Please enter a page number", Snackbar.LENGTH_SHORT).show();
				} else {
					int jumpToPage = Integer.valueOf(etJumpToPage.getText().toString());

					if (jumpToPage < 1 || jumpToPage > contentModelList.size()) {
						if(getView() != null)
							Snackbar.make(getView(), "Please enter a number within range", Snackbar.LENGTH_SHORT).show();
					} else {
						viewPager.setCurrentItem(jumpToPage - 1);
						adapter.notifyDataSetChanged();
					}
				}
			}
		});

        getContent();

        return rootview;
    }

	private void getContent() {

		final RequestQueue content_queue = Volley.newRequestQueue(getContext());

		StringRequest checkFolderContent_req = new StringRequest(Request.Method.POST, url + "getFolderContent.php", new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.getString("status").equals("Success")){
						JSONArray folderList = jsonObject.getJSONArray("folderList");
						contentModelList.clear();

						for (int i = 0; i < folderList.length(); i++) {
							JSONObject jsonObject1 = folderList.getJSONObject(i);

							StudentLearningGalleryContentModel model = new StudentLearningGalleryContentModel(
									jsonObject1.getString("pageNumber"),
									jsonObject1.getString("name"),
									jsonObject1.getString("media"),
									jsonObject1.getString("instructions")

							);
							contentModelList.add(model);
						}
						adapter = new StudentLearningGallerySwipeAdapter(contentModelList, rootview.getContext(), getFragmentManager());
						viewPager.setAdapter(adapter);

					} else if (jsonObject.getString("status").equals("No Record Found")){
						if(getView() != null)
							Snackbar.make(getView(), "Folder is empty!",Snackbar.LENGTH_SHORT).show();
					} else if (jsonObject.getString("status").equals("No Record Found")){
						if(getView() != null)
							Snackbar.make(getView(), "DB error!",Snackbar.LENGTH_SHORT).show();
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
				params.put("job", "retrieve");
				params.put("folderID", folderID);
				return params;
			}
		};

		content_queue.add(checkFolderContent_req);

		content_queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<StringRequest>() {
			@Override
			public void onRequestFinished(Request<StringRequest> request) {
				tvTotalPages.setText("/ " + String.valueOf(contentModelList.size()));
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		((MainActivity)getActivity()).enableDrawer();
	}
}
