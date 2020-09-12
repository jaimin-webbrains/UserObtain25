package com.userobtain25.ui.home.account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.userobtain25.R;
import com.userobtain25.api.RetrofitHelper;
import com.userobtain25.model.SuccessModel;
import com.userobtain25.model.account.ResponseAcceptedOrRejected;
import com.userobtain25.model.account.ResponseAcceptedOrRejected_;
import com.userobtain25.model.login.LoginModel;
import com.userobtain25.utils.PrefUtils;
import com.userobtain25.utils.ViewDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingRequestFragmentFragment extends Fragment {

    protected ViewDialog viewDialog;
    LoginModel loginModel;
    Dialog dialog;
    String resro_id, id;
    private RecyclerView myFriendsRecyclerView;
    private MyCustomAdapter myCustomAdapter;
    private ArrayList<ResponseAcceptedOrRejected_> resultDisplayRestaurantCoupon_s = new ArrayList<>();
    private SwipeRefreshLayout contentHome_SwipeRefresh;
    private int mYear, mMonth, mDay, mHour, mMinute;


    public PendingRequestFragmentFragment() {
        // Required empty public constructor
    }

    public static PendingRequestFragmentFragment newInstance() {

        Bundle args = new Bundle();

        PendingRequestFragmentFragment fragment = new PendingRequestFragmentFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_pending_request_fragment, container, false);
        loginModel = PrefUtils.getUser(getActivity());
        viewDialog = new ViewDialog(getContext());
        viewDialog.setCancelable(false);
        contentHome_SwipeRefresh = rootView.findViewById(R.id.contentHome_SwipeRefresh);
        contentHome_SwipeRefresh.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED);

        myFriendsRecyclerView = rootView.findViewById(R.id.myFriendsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        myFriendsRecyclerView.setLayoutManager(layoutManager);
        myFriendsRecyclerView.setHasFixedSize(true);

        GetReceiveRequest();
        contentHome_SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contentHome_SwipeRefresh.setRefreshing(false);
                GetReceiveRequest();


            }
        });
        return rootView;
    }

    private void GetReceiveRequest() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", loginModel.getSessionData().getId() + "");
        hashMap.put("status", "2");
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

            if (datum.getStatus().equals("2")) {
                holder.txtCName.setText(datum.getName() + "");
                holder.txtPerson.setText(datum.getNumMember() + "");
                holder.txtDate.setText(datum.getDate() + " " + datum.getTime() + "");
                resro_id = datum.getRestoId();
                id = datum.getId();
                holder.label_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog diaBox = AskOption();
                        diaBox.show();


                    }

                    private AlertDialog AskOption() {
                        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                                .setTitle("Delete Request")
                                .setMessage("Do you want to Delete Request?")


                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //your deleting code
                                        showProgressDialog();
                                        final HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", datum.getId() + "");
                                        Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).DeleteUserRequest(hashMap);
                                        marqueCall.enqueue(new Callback<SuccessModel>() {
                                            @Override
                                            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                                                SuccessModel object = response.body();
                                                hideProgressDialog();

                                                Log.e("TAG", "Delete_Response : " + new Gson().toJson(response.body()));
                                                if (object != null && object.getError() == false) {
                                                    holder.l1.setVisibility(View.GONE);
                                                    Toast.makeText(getContext(), object.getMsg() + "", Toast.LENGTH_SHORT).show();
                                                    GetReceiveRequest();
                                                } else if (object != null && object.getError() == true) {
                                                    Toast.makeText(getActivity(), object.getMsg(), Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(getContext(), jObjError.getJSONObject("errors") + "", Toast.LENGTH_LONG).show();

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    try {

                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                                                t.printStackTrace();
                                                hideProgressDialog();

                                                Log.e("ChatV_Response", t.getMessage() + "");
                                            }
                                        });
                                        dialog.dismiss();
                                    }

                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();

                                    }
                                })
                                .create();
                        return myQuittingDialogBox;
                    }
                });
                holder.label_po_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editRequest();
                    }

                    private void editRequest() {
                        dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                        dialog.setContentView(R.layout.dialog_send_request);
                        dialog.setCancelable(true);

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                        final EditText editN_m = dialog.findViewById(R.id.editN_m);
                        final EditText editDate = dialog.findViewById(R.id.editDate);
                        final EditText editTime = dialog.findViewById(R.id.editTime);
                        final Button btn_update = dialog.findViewById(R.id.btn_update);
                        editN_m.setText(datum.getNumMember() + "");
                        editDate.setText(datum.getDate() + " ");
                        editTime.setText(datum.getTime() + "");
                        editDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get Current Date
                                final Calendar c = Calendar.getInstance();
                                mYear = c.get(Calendar.YEAR);
                                mMonth = c.get(Calendar.MONTH);
                                mDay = c.get(Calendar.DAY_OF_MONTH);


                                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme
                                        , new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {

                                        editDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                    }
                                }, mYear, mMonth, mDay);
                                datePickerDialog.show();

                            }
                        });
                        editTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get Current Time
                                final Calendar c = Calendar.getInstance();
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme
                                        , new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        editTime.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                                timePickerDialog.show();

                            }
                        });

                        btn_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String member = editN_m.getText().toString().trim();
                                final String date = editDate.getText().toString().trim();
                                final String time = editTime.getText().toString().trim();


                                if (member.isEmpty()) {
                                    editN_m.setError("Number Of Member Required");
                                    editN_m.requestFocus();
                                    return;
                                }
                                if (date.isEmpty()) {
                                    editDate.setError("Date Required");
                                    editDate.requestFocus();
                                    return;
                                }
                                if (time.isEmpty()) {
                                    editTime.setError("Time Required");
                                    editTime.requestFocus();
                                    return;
                                } else {

                                    HashMap<String, String> hashMap = new HashMap<>();
                                    hashMap.put("user_id", loginModel.getSessionData().getId() + "");
                                    hashMap.put("resto_id", resro_id + "");
                                    hashMap.put("num_member", member + "");
                                    hashMap.put("date_and_time", date + time + "");
                                    hashMap.put("id", id + "");
                                    showProgressDialog();
                                    Log.e("GAYA", hashMap + "");
                                    Call<SuccessModel> marqueCall = RetrofitHelper.createService(RetrofitHelper.Service.class).EditUserRequest(hashMap);
                                    marqueCall.enqueue(new Callback<SuccessModel>() {
                                        @Override
                                        public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                                            SuccessModel object = response.body();
                                            hideProgressDialog();
                                            Log.e("TAG", "ChatV_Response : " + new Gson().toJson(response.body()));
                                            if (object != null && object.getError() == false) {
                                                dialog.dismiss();
                                                GetReceiveRequest();
                                                Toast.makeText(getActivity(), object.getMsg(), Toast.LENGTH_SHORT).show();

                                            } else if (object != null && object.getError() == true) {
                                                dialog.dismiss();
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
                                        public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                                            t.printStackTrace();
                                            hideProgressDialog();
                                            Log.e("ChatV_Response", t.getMessage() + "");
                                        }
                                    });
                                }

                            }
                        });

                        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                    }

                });

            }

        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView txtCName, txtPerson, txtDate;
            ImageButton label_po_edit, label_delete;
            LinearLayout l1;

            public MyViewHolder(View view) {
                super(view);


                txtCName = view.findViewById(R.id.txtCName);
                txtPerson = view.findViewById(R.id.txtPerson);
                txtDate = view.findViewById(R.id.txtDate);
                label_po_edit = view.findViewById(R.id.label_po_edit);
                label_delete = view.findViewById(R.id.label_delete);
                l1 = view.findViewById(R.id.l1);


            }

        }

    }


}