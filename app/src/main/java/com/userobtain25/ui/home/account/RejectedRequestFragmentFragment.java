package com.userobtain25.ui.home.account;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.account.ResponseAcceptedOrRejected;
import com.userobtain25.model.account.ResponseAcceptedOrRejected_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.AppPreferences;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RejectedRequestFragmentFragment extends Fragment {

    protected ViewDialog viewDialog;
    Dialog dialog;
    LoginModel loginModel;
    String latitude, longitude;
    private RecyclerView myFriendsRecyclerView;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResponseAcceptedOrRejected_> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;

    public RejectedRequestFragmentFragment() {
        // Required empty public constructor
    }

    public static RejectedRequestFragmentFragment newInstance() {

        Bundle args = new Bundle();

        RejectedRequestFragmentFragment fragment = new RejectedRequestFragmentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rejected_request_fragment, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = rootView.findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        myFriendsRecyclerView = rootView.findViewById(R.id.myFriendsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        myFriendsRecyclerView.setLayoutManager(layoutManager);
        myFriendsRecyclerView.setHasFixedSize(true);

        GetRejectedRequest();
        contentHome_SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentHome_SwipeRefresh.setRefreshing(false);
                GetRejectedRequest();


            }
        });
        return rootView;
    }

    private void GetRejectedRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", loginModel.getSessionData().getId() + "");
        hashMap.put("status", "0");

        showProgressDialog();
        Log.e("GAYA", hashMap + "");
        Call<ResponseAcceptedOrRejected> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).RestoDisplayUserRequestAcceptedOrRejected(hashMap);
        marqueCall.enqueue(new Callback<ResponseAcceptedOrRejected>() {
            @Override
            public void onResponse(@NonNull Call<ResponseAcceptedOrRejected> call, @NonNull Response<ResponseAcceptedOrRejected> response) {
                ResponseAcceptedOrRejected object = response.body();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
                Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                if (object != null && object.getError() == false) {

                    resultDisplayRestaurantCoupon_s = object.getResponseRestoDisplayUserRequestAcceptedOrRejected();
                    myCustomAdapter = new MyCustomAdapter(resultDisplayRestaurantCoupon_s);
                    myFriendsRecyclerView.setAdapter(myCustomAdapter);

                } else if (object != null && object.getError() == true) {
                     // Toast.makeText(getActivity(), object.getMessage(), Toast.LENGTH_SHORT).show();
                } else {


                    JSONObject jObjError = null;
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.e("TAG", "PO=> Error " + jObjError.getJSONObject("errors") + "");
                        Toast.makeText(getActivity(), jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseAcceptedOrRejected> call, @NonNull Throwable t) {
                t.printStackTrace();
                hideProgressDialog();
                contentHome_SwipeRefresh.setRefreshing(false);
                Log.e("ChatV_Response", t.getMessage() + "");
            }
        });
    }

    protected void hideProgressDialog() {
        viewDialog.dismiss();
    }

    protected void showProgressDialog() {
        viewDialog.show();
    }

    public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyViewHolder> {

        private ArrayList<ResponseAcceptedOrRejected_> moviesList;

        public MyCustomAdapter(ArrayList<ResponseAcceptedOrRejected_> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyCustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_request_list, parent, false);

            return new MyCustomAdapter.MyViewHolder(itemView);
        }

        public void clear() {
            int size = this.moviesList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    this.moviesList.remove(0);
                }

                this.notifyItemRangeRemoved(0, size);
            }
        }

        @Override
        public void onBindViewHolder(final MyCustomAdapter.MyViewHolder holder, final int position) {


            final ResponseAcceptedOrRejected_ datum = moviesList.get(position);

                if (datum.getStatus().equals("0")) {
                    holder.txtDate.setText(datum.getName() + "");
                    holder.txtPerson.setText(datum.getNumMember() + "");
                    holder.txtDate.setText(datum.getDateAndTime() + "");
                    holder.txtStatus.setVisibility(View.GONE);
                }



        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView txtCName, txtPerson, txtDate, txtStatus;


            public MyViewHolder(View view) {
                super(view);


                txtCName = view.findViewById(R.id.txtCName);
                txtPerson = view.findViewById(R.id.txtPerson);
                txtDate = view.findViewById(R.id.txtDate);


            }

        }

    }

}