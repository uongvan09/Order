package com.rtsoftware.order.view.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rtsoftware.order.R;
import com.rtsoftware.order.model.data.User;
import com.rtsoftware.order.model.tranfer.TranferAboutToMain;
import com.rtsoftware.order.pesenter.PUsers;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AboutFragment extends Fragment {
    Context context;
    View rootView;

    CircleImageView imgAva;
    TextView tvNameHeader;
    TextView tvUserName;
    TextView tvFullName;
    TextView tvEmail;
    TextView tvSdt;
    TextView tvAddress;
    TextView btnLogOut;

    final CharSequence[] options = {"Camera", "Gallery"};
    private static String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context= container.getContext();
        rootView= inflater.inflate(R.layout.fragment_about,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getData();
    }

    private void init(){
        imgAva= rootView.findViewById(R.id.imgAvata);
        tvNameHeader= rootView.findViewById(R.id.tvNameHeader);
        tvUserName= rootView.findViewById(R.id.tvUserName);
        tvFullName= rootView.findViewById(R.id.tvFullName);
        tvEmail= rootView.findViewById(R.id.tvEmail);
        tvSdt= rootView.findViewById(R.id.tvPhoneNumber);
        tvAddress= rootView.findViewById(R.id.tvAddress);
        btnLogOut=rootView.findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                EventBus.getDefault().post(new TranferAboutToMain("HELLO"));
            }
        });
        imgAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Choose Source ");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (options[item].equals("Camera")) {
//                            if (context.allowStoragePermissions(PERMISSIONS, REQUEST_EXTERNAL_STORAGE)) {
//                                photoCameraIntent(context);
//                            }
//                        }
//                        if (options[item].equals("Gallery")) {
//                            photoGallery(context);
//                        }
//                    }
//                });
//                builder.show();
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        StorageReference storageRef = storage.getReferenceFromUrl(Utils.URL_STORAGE_REFERENCE).child(Utils.FOLDER_AVATAR_IMG);
//
//        if (requestCode == IMAGE_GALLERY_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Uri selectedImageUri = data.getData();
//                if (selectedImageUri != null) {
//                    sendFile(storageRef, selectedImageUri);
////                      view.setImageUser(selectedImageUri);
//                } else {
//
//                }
//            }
//        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                if (pathImageCamera != null && pathImageCamera.exists()) {
//                    StorageReference imageCameraRef = storageRef.child(pathImageCamera.getName() + "_camera");
//                    sendFile(imageCameraRef, pathImageCamera, context);
//                } else {
//
//                }
//            }
//        }
//    }
//
//
//    private void sendFile(StorageReference storageReference, final Uri file) {
//        if (storageReference != null) {
//            view.setProgressBar(true);
//            StorageReference imageGalleryRef = storageReference.child(stDateFormat + "_gallery");
//            UploadTask uploadTask = imageGalleryRef.putFile(file);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    view.setProgressBar(false);
//                    view.showToastMessage("Lỗi tải ảnh");
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @SuppressLint("LongLogTag")
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.e(TAG, uri.toString());
//                            view.setImageUser(uri.toString());
//                            view.setLinkAvatar(uri.toString());
//                            view.setProgressBar(false);
//                        }
//                    });
//
//                }
//            });
//        }
//    }
//
//    private void photoCameraIntent(Context context) {
//
//        pathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), stDateFormat + "camera.jpg");
//        if (pathImageCamera.exists()) {
//            pathImageCamera.delete();
//        } else {
//            pathImageCamera.getParentFile().mkdirs();
//        }
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri photoURI = FileProvider.getUriForFile(context,
//                BuildConfig.APPLICATION_ID + ".provider",
//                pathImageCamera);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            ClipData clip =
//                    ClipData.newUri(context.getContentResolver(), "A photo", photoURI);
//
//            intent.setClipData(clip);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } else {
//            List<ResolveInfo> resInfoList =
//                    context.getPackageManager()
//                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//
//            for (ResolveInfo resolveInfo : resInfoList) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                context.grantUriPermission(packageName, photoURI,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }
//        }
//        view.startActivity(intent, IMAGE_CAMERA_REQUEST);
//    }
//
//    private void photoGallery(Context context) {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        context.startActivity(Intent.createChooser(intent, context.getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
//    }


    private void getData(){
        PUsers.IfGetUserRespond ifGetUserRespond= new PUsers.IfGetUserRespond() {
            @Override
            public void onSuccess(User user) {
                tvNameHeader.setText(user.getFullName());
                tvUserName.setText(user.getUserName());
                tvFullName.setText(user.getFullName());
                tvEmail.setText(user.getEmail());
                tvSdt.setText(user.getDateOfBirth());
                tvAddress.setText(user.getPermission());
            }

            @Override
            public void onFailt() {
                Toast.makeText(context, "Lấy thông tin người dùng lỗi. Xin thử lại sau", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new TranferAboutToMain("BYE"));
            }
        };

        PUsers pUsers= new PUsers();
        String uId= FirebaseAuth.getInstance().getUid();
        if (uId!=null){
            pUsers.getUser(uId,ifGetUserRespond);
        }else {
            Toast.makeText(context, "Lấy thông tin người dùng lỗi. Xin thử lại sau", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new TranferAboutToMain("BYE"));
        }
    }

}
