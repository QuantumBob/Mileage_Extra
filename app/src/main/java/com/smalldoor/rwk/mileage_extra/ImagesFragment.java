package com.smalldoor.rwk.mileage_extra;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Shows the map images we have
 */
public class ImagesFragment extends Fragment {

    ImageView imageView;
    int mCurrentImage = 1;

    public ImagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image_slide, container, false);

        imageView = (ImageView)rootView.findViewById(R.id.image_view);


        imageView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSwipeRight() {
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                mCurrentImage = mCurrentImage < 2 ? 3 : mCurrentImage-1;
                switch (mCurrentImage){
                    case 1:
                        imageView.setImageResource(R.drawable.cayton_bay);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.crows_nest);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.blue_dolphin);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.cayton_bay);
                        break;
                }
            }
            @Override
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                mCurrentImage = mCurrentImage < 3 ? mCurrentImage+1 : 1;
                switch (mCurrentImage){
                    case 1:
                        imageView.setImageResource(R.drawable.cayton_bay);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.crows_nest);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.blue_dolphin);
                        break;
                    default:
                        imageView.setImageResource(R.drawable.cayton_bay);
                        break;
                }
            }
            @Override
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

}
