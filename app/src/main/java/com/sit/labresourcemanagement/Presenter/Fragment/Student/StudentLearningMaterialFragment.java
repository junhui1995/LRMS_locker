package com.sit.labresourcemanagement.Presenter.Fragment.Student;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sit.labresourcemanagement.Presenter.Adapter.StudentLearningGalleryContentAdapter;
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

public class StudentLearningMaterialFragment extends Fragment {

    private View rootview;

    String folderID;
    RecyclerView recyclerView;
    TextView tvTotalPages;
    EditText etJumpToPage;
    Button btnJump;
	String url = ApiRoutes.getUrl();
	List<StudentLearningGalleryContentModel> contentModelList = new ArrayList<>();
	RecyclerView.Adapter adapter;

    public StudentLearningMaterialFragment() {
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
        rootview = inflater.inflate(R.layout.fragment_student_manual, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerView_Student_Material);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
		tvTotalPages = (TextView) rootview.findViewById(R.id.textViewPageLimit);
		etJumpToPage = (EditText) rootview.findViewById(R.id.editTextJumpPage);
		btnJump = (Button) rootview.findViewById(R.id.buttonJump);

		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);

				if (newState == RecyclerView.SCROLL_STATE_IDLE){
					int page = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
					etJumpToPage.setText(String.valueOf(page + 1));
				}
			}
		});


        btnJump.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//Close the keyboard
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				if (etJumpToPage.getText().toString().isEmpty()){
					if(getView() != null)
						Snackbar.make(getView(), "Please enter a page number", Snackbar.LENGTH_SHORT).show();
				} else {
					int jumpToPage = Integer.valueOf(etJumpToPage.getText().toString());

					if (jumpToPage < 1 || jumpToPage > contentModelList.size()) {
						if(getView() != null)
							Snackbar.make(getView(), "Please enter a number within range", Snackbar.LENGTH_SHORT).show();
					} else {
						recyclerView.scrollToPosition(jumpToPage - 1);
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
						adapter = new StudentLearningGalleryContentAdapter(contentModelList, rootview.getContext(), getFragmentManager());
						recyclerView.setAdapter(adapter);

					} else if (jsonObject.getString("status").equals("No Record Found")){
						if(getView() != null)
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
