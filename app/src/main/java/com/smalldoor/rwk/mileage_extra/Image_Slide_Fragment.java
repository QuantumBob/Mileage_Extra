package com.smalldoor.rwk.mileage_extra;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Image_Slide_Fragment extends Fragment {

    private int mPosition;

    public Image_Slide_Fragment() {
        // Required empty public constructor
    }

//    public static Image_Slide_Fragment


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt("pos");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_slide, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.image_view);

        switch (mPosition){
            case 0:
                imageView.setBackgroundResource(R.drawable.cayton_bay);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.crows_nest);
                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.blue_dolphin);
                break;
            default:
                imageView.setBackgroundResource(R.drawable.cayton_bay);
                break;

        }

        return view;

    }

}
