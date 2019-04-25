package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by AswinBalaji on 03-Nov-17.
 */

public class IdealBodyWeight extends Fragment {


    String number;
    EditText height, weight;
    TextView calorie_counter;
    Spinner spin;
    String gender;
    //---create an anonymous class to act as a button click listener---
    public View.OnClickListener btnListener = new View.OnClickListener() {


        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            int weight1, answer = 0;
            int height1;

            try {
                weight1 = Integer.parseInt(weight.getText().toString().trim());
                height1 = Integer.parseInt(height.getText().toString().trim());

                if (weight1 != 0 && height1 != 0)
                    if (gender.contains("ஆண்")) {
                        answer = (int) Math.round((0.33929 * height1) + (0.32810 * weight1) - (29.5336));
                    } else if (gender.contains("பெண்")) {
                        answer = (int) Math.round((0.41813 * height1) + (0.29569 * weight1) - (43.2933));
                    }
                number = answer + " " + getString(R.string.weight_unit);
                calorie_counter.setText("" + number);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.idealweight_calculator, container, false);

        //---the button is wired to an event handler---
        Button btn1 = rootView.findViewById(R.id.button1);
        height = rootView.findViewById(R.id.EditText02);
        weight = rootView.findViewById(R.id.EditText01);

        btn1.setOnClickListener(btnListener);
        calorie_counter = rootView.findViewById(R.id.textView6);

        //Creating a spinner and adding two items to it.
        spin = rootView.findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new SpinnerListener());
        return rootView;
    }

    //spinner listener for values male and female
    public class SpinnerListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            gender = parent.getItemAtPosition(pos).toString();
        }


        public void onItemSelected1(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }


    }
}
