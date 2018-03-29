package com.example.mark.activityplanner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mark.activityplanner.network.RetrofitRequest;
import com.example.mark.activityplanner.utils.Activity;
import com.example.mark.activityplanner.utils.Upload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class EditProfile extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Button btn_update, btn_save, btn_upload;
    ArrayList<String> selectedItems = new ArrayList<>();
    private TextView tv, tv_switch;
    private CompositeSubscription mSubscriptions;
    private ProgressBar mProgressbar, mProgressbar6;
    private static final int CHOOSE_IMAGE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static  int chooser = 0;
    ImageView imageView, iv_gallery;
    Uri uriProfileImage;
    EditText et_imageName;
    String profileImageUrl;
    FirebaseAuth mAuth;
    ImageView ibtn_upArrow;
    ListView listView;
    static int imageInt = 0;
    ArrayAdapter<String> adapter;
    String[] items = {"Airsoft", "American Football", "Archery", "Badminton", "Baseball", "Basketball", "BMX", "Boxing", "Canoe / Kayak", "Climbing", "Cricket", "Curling", "Cycling", "Darts", "Diving", "Dodgeball", "Equestrian", "Fencing", "GAA", "Golf", "Gymnastics", "Handball", "Hiking", "Hockey", "Hurling", "Judo", "Karate", "Motocross", "Mountain Biking", "Mountain Boarding", "Netball", "Paintball", "Rollerblading", "Rowing", "Rugby", "Running", "Sailing", "Scootering", "Shooting", "Skateboarding", "Skiing", "Snooker", "Snowboarding", "Soccer / Football", "Swimming", "Surfing", "Squash", "Table Tennis", "Taekwondo", "Tennis", "Track & Field", "Triathlon", "Ultimate Frisbee", "Unicycling", "Volleyball", "Wakeboarding", "Walking", "Water Polo", "Weightlifting", "Wind Surfing", "Wrestling"};
    private String profilePrefString;
    private String imageURL;
    private  StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private Switch aSwitch;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private String username;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mSubscriptions = new CompositeSubscription();
        mAuth = FirebaseAuth.getInstance();

        imageURL = sharedPref.getString("profileImage","");
        username = sharedPref.getString("username","");

        mStorageRef = FirebaseStorage.getInstance().getReference("users/"+username+"/images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users/"+username+"/images");

        btn_update = findViewById(R.id.btn_update_id);
        btn_save = findViewById(R.id.btn_save_id);
        btn_upload = findViewById(R.id.btn_upload_id);
        et_imageName = findViewById(R.id.et_imageText_id);
        tv = findViewById(R.id.tv_activities);
        mProgressbar = findViewById(R.id.progressBar3);
        mProgressbar6 = findViewById(R.id.progressBar6);
        imageView = findViewById(R.id.image_choose_id);
        iv_gallery = findViewById(R.id.iv_gallery_id);
        aSwitch = findViewById(R.id.switch1);
        tv_switch = findViewById(R.id.tv_switch_id);

        btn_update.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        imageView.setOnClickListener(this);
        iv_gallery.setOnClickListener(this);
        ibtn_upArrow = findViewById(R.id.btn_up_arrow_id);
        ibtn_upArrow.setOnClickListener(this);
        aSwitch.setOnCheckedChangeListener(this);

        if(imageURL != null) {
            get_firebase_image();
        }

        if(sharedPref.contains("Activities")) {
            Log.d("myTag", "Trying");
            Set<String> set = sharedPref.getStringSet("Activities", null);
            List<String> sample = new ArrayList<String>(set);

            sample.sort(new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return lhs.compareTo(rhs);
                }
            });

            tv.setText(sample.toString().replace("[", "").replace("]",""));
        }

        if(sharedPref.contains("switch")){
            Boolean switchPos = sharedPref.getBoolean("switch", false);
            aSwitch.setChecked(switchPos);
        }

        listView = findViewById(R.id.checkable_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txt_lan, items);
        listView.setAdapter(adapter);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem)){
                    selectedItems.remove(selectedItem);
                }
                else{
                    selectedItems.add(selectedItem);
                }
            }
        });
    }

    private void get_firebase_image() {

        Glide.with(this)
                .load(imageURL)
                .into(imageView);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_id: {

                Set<String> set = new HashSet<String>();
                Collection<String> removeCandidates = new ArrayList<>();
                Collection<String> removeCandidates2 = new ArrayList<>();
                if(sharedPref.contains("Activities")) {
                    set = sharedPref.getStringSet("Activities", null);

                    for(String o : selectedItems){
                        for(String p : set) {
                            if (o.equals(p)){
                                removeCandidates.add(o);
                                removeCandidates2.add(p);
                                break;
                            }
                        }
                    }
                    selectedItems.removeAll(removeCandidates);
                    set.removeAll(removeCandidates2);
                    set.addAll(selectedItems);
                }
                else{
                    set.addAll(selectedItems);
                }
                editor.putStringSet("Activities", set);
                editor.apply();

                String username = sharedPref.getString("username", null);

                Activity activity = new Activity(username, set);
                add_activities(activity);

                setResult(10001);
                finish();
                startActivity(getIntent());

                break;
            }
            case R.id.image_choose_id: {
                showImageChooser(0);

                break;
            }
            case R.id.btn_save_id: {
                saveUserInformation();

                break;
            }
            case R.id.btn_up_arrow_id: {

                if(imageInt == 0) {
                    ibtn_upArrow.setBackgroundResource(R.drawable.ic_drop_down);
                    imageInt = 1;
                    setListViewHeightBasedOnChildren(listView);

                }
                else{
                    imageInt = 0;
                    finish();
                    startActivity(getIntent());
                }


                break;
            }
            case R.id.iv_gallery_id: {

                showImageChooser(1);

                break;
            }
            case R.id.btn_upload_id: {

                String displayName = et_imageName.getText().toString();
                if(displayName.isEmpty()){
                    et_imageName.setError("Name required");
                    et_imageName.requestFocus();
                    return;
                }
                else {

                    //uploadImageToFirebaseStorage();
                    if(mUploadTask != null && mUploadTask.isInProgress()){
                        Toast.makeText(EditProfile.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    }else {
                        mProgressbar6.setVisibility(View.VISIBLE);
                        uploadFile();
                    }
                }

                break;
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if(mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressbar6.setProgress(0);
                                }
                            }, 500);

                            mProgressbar6.setVisibility(View.GONE);
                            Toast.makeText(EditProfile.this, "Upload Scucessful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(et_imageName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressbar6.setProgress((int) progress);
                        }
                    });
        }else{
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInformation() {

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null && profileImageUrl != null){
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                mProgressbar6.setVisibility(View.GONE);
                                if(chooser == 0){
                                    editor.putString("profileImage", profileImageUrl);
                                    editor.apply();
                                }
                            }
                        }
                    });
        }
    }

    private void add_activities(Activity activity) {
        mProgressbar.setVisibility(View.VISIBLE);

        mSubscriptions.add(RetrofitRequest.getRetrofit().addActivities(activity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response<ResponseBody> responseBodyResponse) {
        mProgressbar.setVisibility(View.GONE);
        Log.d("Response", responseBodyResponse.message());
        finish();

    }

    private void handleError(Throwable error) {
        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage("What " + errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
            Log.d("MyTag", error.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null){
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                if(chooser == 0) {
                    imageView.setImageBitmap(bitmap);
                }
                else{
                    iv_gallery.setImageBitmap(bitmap);
                }
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData()!=null){
            mImageUri = data.getData();

            Glide.with(this).load(mImageUri).into(iv_gallery);
        }
    }

    private void uploadImageToFirebaseStorage() {
        String email = sharedPref.getString("email","");
        StorageReference profileImageRef;
        DatabaseReference databaseRef;
        if (chooser == 0) {
            profileImageRef = FirebaseStorage.getInstance().getReference("users/"+email+"/profilepics/"+System.currentTimeMillis() + ".jpg");
        }
        else{
            String displayName = et_imageName.getText().toString();
            if(displayName.isEmpty()){
                et_imageName.setError("Name required");
                et_imageName.requestFocus();
                return;
            }
            profileImageRef = FirebaseStorage.getInstance().getReference("users/"+email+"/images/"+displayName+ ".jpg");
            databaseRef = FirebaseDatabase.getInstance().getReference("users/"+email+"/images/"+displayName+ ".jpg");
        }

        if(uriProfileImage != null){
            mProgressbar6.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressbar6.setVisibility(View.GONE);
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();


                    if(chooser == 1){
                        saveUserInformation();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressbar.setVisibility(View.GONE);
                    Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImageChooser(int i){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if(i == 0) {
            chooser = 0;
            startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
        }
        else{
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            chooser = 1;
        }
    }

    private void add_activities2(Set<String> set, String username) {
        ServerRequests server_requests = new ServerRequests(this);
        server_requests.add_activities(set, username, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                try{
                    Log.d("myTag", returned_string);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_edit_profile), message, Snackbar.LENGTH_SHORT).show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) view.setLayoutParams(new
                    ViewGroup.LayoutParams(desiredWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() *
                (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);



        listView.requestLayout();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int choice;
        if(aSwitch.isChecked()){
            tv_switch.setText("Profile Currently Public");
            editor.putBoolean("switch", true);
            editor.apply();
            choice = 2;
            make_public(choice);
        }else{
            tv_switch.setText("Profile Currently Private");
            editor.putBoolean("switch", false);
            editor.apply();
            choice = 1;
            make_public(choice);
        }
    }

    private void make_public(int choice) {
        String choiceStr = String.valueOf(choice);

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.makePublic(username, choiceStr, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                if(returned_string.equals("Successful")){
                    Toast.makeText(EditProfile.this, "Privacy Updated", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
