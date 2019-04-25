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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by AswinBalaji on 03-Nov-17.
 */

public class BodyFatCalculator extends Fragment {


    String number;
    EditText bmi, age;
    TextView calorie_counter;
    Spinner spin;
    String gender;
    ImageView Chart;
    //---create an anonymous class to act as a button click listener---
    public View.OnClickListener btnListener = new View.OnClickListener() {


        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            int age1, answer = 0;
            float bmi1;

            try {
                age1 = Integer.parseInt(age.getText().toString().trim());
                bmi1 = Float.parseFloat(bmi.getText().toString().trim());

                if (age1 != 0 && bmi1 != 0)
                    if (gender.contains("ஆண்")) {
                        if (age1 <= 15) {
                            answer = (int) Math.round((1.51 * bmi1) - (0.70 * age1) - (3.6 * 1) + 1.4);
                        } else {
                            answer = (int) Math.round((1.20 * bmi1) + (0.23 * age1) - (10.8 * 1) - 5.4);
                        }
                    } else if (gender.contains("பெண்")) {
                        if (age1 <= 15) {
                            answer = (int) Math.round((1.51 * bmi1) - (0.70 * age1) - (3.6 * 0) + 1.4);
                        } else {
                            answer = (int) Math.round((1.20 * bmi1) + (0.23 * age1) - (10.8 * 0) - 5.4);
                        }
                    }
                number = answer + " %";
                calorie_counter.setText("" + number);
                if (number != null) {
                    Chart.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bodyfat_calculator, container, false);

        //---the button is wired to an event handler---
        Button btn1 = rootView.findViewById(R.id.button1);
        bmi = rootView.findViewById(R.id.EditText02);
        age = rootView.findViewById(R.id.EditText01);
        Chart = rootView.findViewById(R.id.chart);

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
