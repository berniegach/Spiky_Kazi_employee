package com.spikingacacia.spikykaziemployee.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.spikingacacia.spikykaziemployee.JSONParser;
import com.spikingacacia.spikykaziemployee.LoginActivity;
import com.spikingacacia.spikykaziemployee.Preferences;
import com.spikingacacia.spikykaziemployee.R;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static com.spikingacacia.spikykaziemployee.LoginActivity.base_url;

public class UPAddF extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String url_insert_profile= LoginActivity.base_url+"insert_user_profile.php";
    private String url_update_trade=base_url+"u_update_trade.php";
    private JSONParser jsonParser;
    private String TAG_SUCCESS="success";
    private String TAG_MESSAGE="message";
    private LinearLayout as;
    private LinearLayout create;
    private LinearLayout skills;
    private LinearLayout equipment;
    private Spinner spinner;
    private String companyId;
    private boolean justStarted=true;
    private Preferences preferences;

    public UPAddF()
    {
        // Required empty public constructor
    }
    public static UPAddF newInstance(String param1, String param2)
    {
        UPAddF fragment = new UPAddF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        jsonParser=new JSONParser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.f_upadd, container, false);
        preferences= new Preferences(getContext());
        as=view.findViewById(R.id.as);
        spinner=view.findViewById(R.id.spinner);
        create=view.findViewById(R.id.create);
        skills=view.findViewById(R.id.skills);
        equipment=view.findViewById(R.id.equipments);

        if(mParam1.contentEquals("add"))
        {
            as.setVisibility(View.GONE);
            create.setVisibility(View.VISIBLE);
            skills.setVisibility(View.GONE);
            equipment.setVisibility(View.GONE);
        }
        else if(mParam1.contentEquals("edit"))
        {
            as.setVisibility(View.VISIBLE);
            create.setVisibility(View.GONE);
            skills.setVisibility(View.VISIBLE);
            equipment.setVisibility(View.VISIBLE);
            addTrades();
        }
        skills.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(1);
            }
        });
        equipment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mListener!=null)
                    mListener.onMenuClicked(2);
            }
        });
        create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                createProfile();
            }
        });

        if(!preferences.isDark_theme_enabled())
        {
            create.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            skills.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            equipment.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
            as.setBackgroundColor(getResources().getColor(R.color.secondary_background_light));
        }
        return view;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener
    {
        void onMenuClicked(int id);
        void onProfileAdded();
    }
  private void createProfile()
  {
      new AlertDialog.Builder(getContext())
              .setItems(new String[]{"Press here to enter the Company's Email Address\n(The one the company uses in kazi)."}, new DialogInterface.OnClickListener()
              {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i)
                  {
                      if(i==0)
                      {
                          final android.app.AlertDialog dialog;
                          android.app.AlertDialog.Builder builderPass=new android.app.AlertDialog.Builder(getContext());
                          //builderPass.setTitle("Name?");
                          TextInputLayout textInputLayout=new TextInputLayout(getContext());
                          textInputLayout.setPadding(10,10,10,0);
                          textInputLayout.setGravity(Gravity.CENTER);
                          final EditText editText=new EditText(getContext());
                          editText.setPadding(20,10,20,10);
                          editText.setTextSize(14);
                          textInputLayout.addView(editText,0,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                          editText.setHint("company's email address");
                          editText.setError(null);
                          LinearLayout layout=new LinearLayout(getContext());
                          layout.setOrientation(LinearLayout.VERTICAL);
                          layout.addView(textInputLayout);
                          builderPass.setView(layout);
                          builderPass.setPositiveButton("Create", null);
                          builderPass.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                          {
                              @Override
                              public void onClick(DialogInterface dialogInterface, int i)
                              {
                                  dialogInterface.dismiss();
                              }
                          });
                          dialog=builderPass.create();
                          dialog.setOnShowListener(new DialogInterface.OnShowListener()
                          {
                              @Override
                              public void onShow(DialogInterface dialogInterface)
                              {
                                  Button button=((android.app.AlertDialog)dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                                  button.setOnClickListener(new View.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(View view)
                                      {
                                          String name=editText.getText().toString();
                                          if(!name.contains("@"))
                                          {
                                              editText.setError("Not a valid email address");
                                          }

                                          else
                                          {
                                              new InsertProfileTask(name).execute((Void)null);
                                              dialog.dismiss();
                                          }
                                      }
                                  });
                              }
                          });
                          dialog.show();
                      }
                  }
              }).create().show();
  }
  public class InsertProfileTask extends AsyncTask<Void, Void, Boolean>
  {
        private String email;
        private int success;
        InsertProfileTask(final String email)
        {
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();            this.email=email;
            Log.d("INSERTPROFILE"," started...");
        }
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("email",email));
            info.add(new BasicNameValuePair("userid",String.valueOf(LoginActivity.userAccount.getId())));
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_insert_profile,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    companyId=jsonObject.getString("id");
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean successful)
        {
            if(successful)
            {
                if(mListener!=null)
                    mListener.onProfileAdded();
            }
            else if(success==-1 || success==-2)
            {
                Toast.makeText(getContext(),"Email not found",Toast.LENGTH_SHORT).show();
            }
            else if(success==-3)
            {
                Toast.makeText(getContext(),"Error please try again",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(),"Error please try again",Toast.LENGTH_SHORT).show();
            }

        }
    }
  public class UpdateTradeTask extends AsyncTask<Void, Void, Boolean>
    {
        private int success=0;
        private final String mId;
        private final String mUserid;
        private  String mTradeName;
        private  String mNewTradeName;

        UpdateTradeTask(String id,String userid, String tradename,final String newtradename)
        {
            mId=id;
            mUserid=userid;
            mTradeName=tradename;
            mTradeName=mTradeName.toLowerCase().replace(" ","_");
            mNewTradeName=newtradename;
            mNewTradeName=mNewTradeName.toLowerCase().replace(" ","_");
        }
        @Override
        protected void onPreExecute()
        {
            Toast.makeText(getContext(),"Updating started. Please wait...",Toast.LENGTH_SHORT).show();
            Log.d("UPDATING TRADE: ","updating....");
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //building parameters
            List<NameValuePair> info=new ArrayList<NameValuePair>();
            info.add(new BasicNameValuePair("id",mId));
            info.add(new BasicNameValuePair("userid",mUserid));
            info.add(new BasicNameValuePair("tradename",mTradeName));
            info.add(new BasicNameValuePair("newtradename",mNewTradeName));
            //getting all account details by making HTTP request
            JSONObject jsonObject= jsonParser.makeHttpRequest(url_update_trade,"POST",info);
            try
            {
                success=jsonObject.getInt(TAG_SUCCESS);
                if(success==1)
                {
                    return true;
                }
                else
                {
                    String message=jsonObject.getString(TAG_MESSAGE);
                    Log.e(TAG_MESSAGE,""+message);
                    return false;
                }
            }
            catch (JSONException e)
            {
                Log.e("JSON",""+e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean successful) {
            Log.d("UPDATING TRADE: ","finished....");
            if (successful)
            {
               LoginActivity.userAccount.setPosition(mNewTradeName);
               Toast.makeText(getContext(),"You have succesfully changed your \njob position",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(),"Error changing your \njob position",Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled()
        {
            //mAuthTaskU = null;
        }
    }
  private void addTrades()
    {
        int pos=0;
        final String[] trades=new String[LoginActivity.tradesList.size()];
        Iterator iterator= LoginActivity.tradesList.entrySet().iterator();
        while (iterator.hasNext())
        {
            Log.d("items",Integer.toString(pos));
            LinkedHashMap.Entry<String,Integer>set=(LinkedHashMap.Entry<String, Integer>) iterator.next();
            String name=set.getKey();
            name=name.replace("_"," ");
            Integer count=set.getValue();
            trades[pos]=name;
            pos+=1;
        }
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,trades);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        int index=0;
        for(int count=0; count<trades.length; count+=1)
        {
            String position=trades[count];
            position=position.replace(" ","_");
            if(position.contentEquals(LoginActivity.userAccount.getPosition()))
            {
                index=count;
                break;
            }
        }
        if(!LoginActivity.userAccount.getPosition().contentEquals("null") || !LoginActivity.userAccount.getPosition().contentEquals(""))
        {
            spinner.setSelection(index);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                final int index=i;
                if(justStarted)
                {
                    justStarted=false;
                    return;
                }
                String position=trades[i];
                position=position.replace(" ","_");
                if(position.contentEquals(LoginActivity.userAccount.getPosition()))
                    return;
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to change your job position?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                new UpdateTradeTask(String.valueOf(LoginActivity.contractorAccount.getId()),String.valueOf(LoginActivity.userAccount.getId()),LoginActivity.userAccount.getPosition(),trades[index]).execute((Void)null);
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
    }
}
