package valeriechen1596.myfirstapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by valeriechen on 6/10/16.
 */

public class Adding extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_andrea, container, false);

        Button nextButton = (Button) view.findViewById(R.id.button_first);
        nextButton.setOnClickListener((this));

        return view;
    }

    public void onClick(View v){

    }
}
