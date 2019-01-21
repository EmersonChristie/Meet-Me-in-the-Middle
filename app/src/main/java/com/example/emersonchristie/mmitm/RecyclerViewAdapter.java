package com.example.emersonchristie.mmitm;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    private List<PlacesPOJO.CustomA> stLstStores;
    private List<StoreModel> models;

    private Resources mResources;

    private final OnItemClickListener listener;
    Context mContext;

    private GeoDataClient mGeoDataClient;

    public Bitmap bitM;


    public RecyclerViewAdapter(List<PlacesPOJO.CustomA> stores, List<StoreModel> storeModels, OnItemClickListener listener)  {

        stLstStores = stores;
        models = storeModels;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_row, parent, false);

        mResources = parent.getResources();

        return new MyViewHolder(view);
    }

    private int lastPosition = -1;

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setData(stLstStores.get(holder.getAdapterPosition()), holder, models.get(holder.getAdapterPosition()));

        //TODO added from onclick tut
        holder.bind(stLstStores.get(holder.getAdapterPosition()), models.get(holder.getAdapterPosition()), listener);

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        .startAnimation(animation);
//        lastPosition = position;
    }



    //TODO added onclick from custom adapter

    public interface OnItemClickListener {
        void onItemClick(PlacesPOJO.CustomA info, StoreModel item);
    }


    @Override
    public int getItemCount() {
        return Math.min(10, stLstStores.size());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView txtStoreName;
        TextView txtStoreAddr;
        TextView txtStoreDist;
        ImageView imgPhoto;
        StoreModel model;
        RatingBar mRatingBar;


        public MyViewHolder(final View itemView) {
            super(itemView);

            this.txtStoreDist = (TextView) itemView.findViewById(R.id.txtStoreDist);
            this.txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
            this.txtStoreAddr = (TextView) itemView.findViewById(R.id.txtStoreAddr);
            //this.txtRating = (TextView) itemView.findViewById(R.id.txtRating);
            this.imgPhoto = (ImageView) itemView.findViewById(R.id.imgPhoto);
            this.mRatingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);

        }

        //bind method added from onclick tut
        public void bind(final PlacesPOJO.CustomA info, final StoreModel item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(info, item);
                }
            });
        }


        public void setData(PlacesPOJO.CustomA info, MyViewHolder holder, StoreModel storeModel) {


            this.model = storeModel;

            Bitmap bitmap = model.placePhoto;
//            String rating = info.rating.toString();
            float cornerRadius = 10.0f;

            // Initialize a new RoundedBitmapDrawable object to make ImageView rounded corners
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                    mResources,
                    bitmap
            );

            // Set the RoundedBitmapDrawable corners radius
            roundedBitmapDrawable.setCornerRadius(cornerRadius);

                /*
                    setAntiAlias(boolean aa)
                        Enables or disables anti-aliasing for this drawable.
                */
            roundedBitmapDrawable.setAntiAlias(true);

            // Set the ImageView image as drawable object
            holder.imgPhoto.setImageDrawable(roundedBitmapDrawable);

            holder.txtStoreDist.setText("Meet in " + model.duration);
            holder.txtStoreName.setText(info.name);
            holder.txtStoreAddr.setText(info.vicinity);
            //holder.txtRating.setText(rating);
            //getPhotos(model.placeID);
            //holder.imgPhoto.setImageBitmap(roundedBitmapDrawable);
            holder.mRatingBar.setNumStars(5);
            holder.mRatingBar.setRating(info.rating.floatValue());
            holder.mRatingBar.setIsIndicator(true);
        }

        //function to get photo bitmap from place api
        // Request photos and metadata for the specified place.
//        private void getPhotos(String ID) {
//
//            final String placeId = ID;
//            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
//            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
//                    // Get the list of photos.
//                    PlacePhotoMetadataResponse photos = task.getResult();
//                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
//                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                    // Get the first photo in the list.
//                    //check for null photo
//                    if (photoMetadataBuffer.getCount() > 0) {
//                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
//
//                        // Get the attribution text.
//                        CharSequence attribution = photoMetadata.getAttributions();
//                        // Get a full-size bitmap for the photo.
//                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
//                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
//                            @Override
//                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
//                                PlacePhotoResponse photo = task.getResult();
//                                bitM = photo.getBitmap();
//                            }
//                        });
//                    }
//                }
//            });
//
//        }

    }

}

