package com.shels.delivery.Fragments;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.DataBaseUtils.ViewModel.DeliveryActsViewModel;
import com.shels.delivery.DialogFactory;
import com.shels.delivery.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentPhotoFragment extends Fragment {

    private CarouselView carouselView;
    private FloatingActionButton floatingActionButton;
    private String documentId;
    private DeliveryActsViewModel deliveryActsViewModel;
    private DeliveryAct deliveryAct;
    private String currentPhotoPath;
    private final int REQUEST_CAMERA = 1;

    public DocumentPhotoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document_photo, container, false);

        deliveryActsViewModel = ViewModelProviders.of(this).get(DeliveryActsViewModel.class);

        documentId = getArguments().getString("documentId");
        deliveryAct = deliveryActsViewModel.getDeliveryActById(documentId);

        floatingActionButton = view.findViewById(R.id.document_photo_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPhoto();
            }
        });

        carouselView = view.findViewById(R.id.document_photo_carouselView);

        displayPictures();

        return view;
    }

    void takeAPhoto(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }else{
            File photoFile = null;

            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = createImageFile();

                if (photoFile != null){
                    Uri photoURI = FileProvider.getUriForFile(getContext(),
                            "com.shels.delivery.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            } catch (ActivityNotFoundException e) {
               DialogFactory.showAlertDialog(getContext(), getResources().getString(R.string.camera_not_found));
            } catch (IOException e) {
                DialogFactory.showAlertDialog(getContext(), e.toString());
            }
        }
    }

    private File createImageFile() throws IOException {
        // Сформируем путь к файлу
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalCacheDir();

        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* суффикс */
                storageDir      /* каталог */
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if (permissions[0].equals(Manifest.permission.CAMERA)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takeAPhoto();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                savePictureToCache();

                displayPictures();
                break;
        }
    }

    void savePictureToCache(){
        if (!currentPhotoPath.isEmpty()) {
            // Сохраним ссылку к файлу в документе
            ArrayList<String> documentPhotos = new ArrayList<>();

            // Заполним новый массив данными по текущений накладной
            documentPhotos.addAll(deliveryAct.getDocumentsPhotos());

            // Добавим ссылку на новую картинку в накладную
            documentPhotos.add(currentPhotoPath);

            // Обновим список картинок в накладной
            deliveryAct.setDocumentsPhotos(documentPhotos);

            // Обновим данные в БД
            deliveryActsViewModel.updateDeliveryAct(deliveryAct);
        }
    }

    void displayPictures(){
        new DisplayPicturesTask().execute();
    }

    private class DisplayPicturesTask extends AsyncTask<Void, Void, Bitmap[]>{
        @Override
        protected Bitmap[] doInBackground(Void... voids) {
            List<String> fileNames = deliveryAct.getDocumentsPhotos();
            Bitmap[] bitmaps = new Bitmap[fileNames.size()];

            for (int i = 0; i < fileNames.size(); i++){
                String fileName = fileNames.get(i);
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);

                bitmaps[i] = bitmap;
            }

            return bitmaps;
        }

        @Override
        protected void onPostExecute(final Bitmap[] bitmaps) {
            if (bitmaps != null && bitmaps.length > 0) {
                for (Bitmap bitmap : bitmaps){
                    if (bitmap != null){
                        carouselView.setImageListener(new ImageListener() {
                            @Override
                            public void setImageForPosition(int position, ImageView imageView) {
                                imageView.setImageBitmap(bitmaps[position]);
                            }
                        });
                        carouselView.setPageCount(bitmaps.length);
                    }
                }
            }
        }
    }

}
