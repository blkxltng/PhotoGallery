package com.blkxltng.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by firej on 10/24/2017.
 */

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static  PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                photoHolder.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    private void setupAdapter() {
        if(isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
//            if((mGalleryItems.size() - (position++)) < 10) {
//                for(int i = position; i > (position - 10); i--) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//                for(int i = position; i < mGalleryItems.size(); i++) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//            } else if(position < 10) {
//                for(int i = position; i >= 0; i--) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//                for(int i = position; i < (position + 10); i++) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//            } else {
//                for(int i = position; i > (position - 10); i--) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//                for(int i = position; i < (position + 10); i++) {
//                    GalleryItem galleryItem = mGalleryItems.get(i);
//                    Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
//                    holder.bindDrawable(placeholder);
//                    mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
//                }
//            }

            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.ic_photo_grey_48dp);
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mItems = galleryItems;
            setupAdapter();
        }
    }
}
