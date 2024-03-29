package com.spikingacacia.spikykaziemployee;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceViewHolder;

import net.gotev.uploadservice.Logger;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;

//import androidx.preference.Preference;


/**
 * Created by $USER_NAME on 9/20/2018.
 **/
public class UPreferencePic extends Preference
{
    private static final int PERMISSION_REQUEST_INTERNET=2;
    private static String url_upload_profile_pic= LoginActivity.base_url+"u_upload_profile_pic.php";
    public static ImageView imageView;
    public static TextView textView;
    private static Context context;
    private static JSONParser jsonParser;
    private static String TAG_SUCCESS="success";
    private static String TAG_MESSAGE="message";
    private FragmentManager fragmentManager;
    private Preferences preferences;
    public UPreferencePic(Context context)
    {
        super(context);
        setLayoutResource(R.layout.usettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);
    }

    public UPreferencePic(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        setLayoutResource(R.layout.usettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);
    }
    public UPreferencePic(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        setLayoutResource(R.layout.usettings_profilepic);
        this.context=context;
        fragmentManager=((AppCompatActivity)context).getFragmentManager();
        jsonParser=new JSONParser();
        preferences = new Preferences(context);
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder view)
    {
        super.onBindViewHolder(view);
        imageView=(ImageView)view.findViewById(R.id.imagepic);
        //get the profile pic
        imageView.setImageBitmap(USettingsActivity.profilePic);
        if(!preferences.isDark_theme_enabled())
        {
            view.itemView.setBackgroundColor(context.getResources().getColor(R.color.secondary_background_light));
        }
        view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                final FragmentManager fragmentManager=((AppCompatActivity)context).getFragmentManager();
                Fragment fragment= GetPicture.newInstance();
                fragmentManager.beginTransaction().add(fragment,"AB").commit();
                fragmentManager.executePendingTransactions();
                Intent intent=new Intent();
                //show only images
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/jpeg"});
                intent.setAction(Intent.ACTION_GET_CONTENT);
                fragment.startActivityForResult(Intent.createChooser(intent,"Select profile Image in jpg format"),1);
                notifyChanged();
            }
        });

    }

    public static class GetPicture extends Fragment
    {
        public static GetPicture newInstance()
        {
            GetPicture getPicture=new GetPicture();
            return getPicture;

        }

        @Override
        public void onActivityResult(final int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode,resultCode,data);
            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                final Uri uri = data.getData();
                try
                {

                    final String path= GetFilePathFromDevice.getPath(context,uri);
                    Log.d("path",path);

                if (true)
                {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                    Toast.makeText(context, "Profile pic changed", Toast.LENGTH_SHORT).show();
                    //upload
                    if (path == null)
                    {
                        Log.e("upload cert","its null");
                    }
                    else
                    {
                        //Uploading code
                        Log.d("uploading","1");
                        try
                        {
                            Logger.setLogLevel(Logger.LogLevel.DEBUG);
                            uploadPic(path);

                        }
                        catch (Exception e)
                        {
                            Log.d("uploading","2");
                            e.printStackTrace();
                        }
                    }
                }
                }
                catch (Exception e)
                {
                    Log.e("bitmap", "" + e.getMessage());
                }
            }
            getFragmentManager().beginTransaction().remove(this).commit();
        }
        private boolean uploadPic(final String location) {
            boolean ok=true;
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                //getting name for the image
                String name="prof_pic";
                //getting the actual path of the image
                // String path=getPath(certUri[index]);
                String path=location;
                if (path == null)
                {
                    Log.e("upload cert","its null");
                }
                else
                {
                    //Uploading code
                    try
                    {
                        String uploadId = UUID.randomUUID().toString();
                        //Creating a multi part request
                        new MultipartUploadRequest(getActivity(), uploadId, url_upload_profile_pic)
                                .addFileToUpload(path, "jpg") //Adding file
                                .addParameter("name", name) //Adding text parameter to the request
                                .addParameter("id",String.valueOf(LoginActivity.userAccount.getId()))
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(2)
                                .setDelegate(new UploadStatusDelegate()
                                {
                                    @Override
                                    public void onProgress(Context context, UploadInfo uploadInfo)
                                    {
                                        Log.d("GOTEV",uploadInfo.toString());
                                    }

                                    @Override
                                    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception)
                                    {
                                        Log.e("GOTEV",uploadInfo.toString()+"\n"+exception.toString()+"\n");
                                    }

                                    @Override
                                    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
                                    {
                                        //JSONObject result = new JSONObject(serverResponse);
                                    }

                                    @Override
                                    public void onCancelled(Context context, UploadInfo uploadInfo)
                                    {
                                        Log.d("GOTEV","cancelled"+uploadInfo.toString());
                                    }
                                })
                                .startUpload(); //Starting the upload
                    }
                    catch (Exception e)
                    {
                        Log.e("image upload",""+e.getMessage());
                        e.printStackTrace();
                        ok=false;
                    }
                }
            }
            //request the permission
            else
            {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_INTERNET);
            }

            return ok;
        }
    }
}
