package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.annotation.SuppressLint;
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
 * Created by AswinBalaji on 31-Aug-17.
 */

public class BMICalculator extends Fragment {

    ImageView Chart;
    EditText weightNum, heightNum1, height2;
    TextView Bmi, PrimeBmi, resultLabel;

    double bmi, bmiprime;
    Spinner spin;
    String units;

    int weight = 0;
    int height = 0;
    int ideal;
    int inch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.bmi_calculator, container, false);

        Chart = rootView.findViewById(R.id.chart);

        weightNum = rootView.findViewById(R.id.weightNum);
        heightNum1 = rootView.findViewById(R.id.heightNum1);
        height2 = rootView.findViewById(R.id.heightNum2);

        Bmi = rootView.findViewById(R.id.resultLabel);

        PrimeBmi = rootView.findViewById(R.id.resultLabel1);
        resultLabel = rootView.findViewById(R.id.resultLabel2);

        //Creating a spinner and adding two items to it.
        spin = rootView.findViewById(R.id.spinner1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.bmi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new SpinnerListener());


        Button bmical = rootView.findViewById(R.id.calcBMI);
        bmical.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                if (units.contains("Kg/cm")) {
                    if (!(weightNum.getText().toString().equals(""))) {
                        weight = Integer.parseInt(weightNum.getText().toString());
                    }
                    if (!(heightNum1.getText().toString().equals(""))) {
                        height = Integer.parseInt(heightNum1.getText().toString());
                    }
                    if (weight != 0 && height != 0)
                        bmi = weight / ((height * 0.01) * (height * 0.01));
                } else if (units.contains("lbs/in")) {
                    if (!(weightNum.getText().toString().equals(""))) {
                        weight = Integer.parseInt(weightNum.getText().toString());
                    }

                    if (!(heightNum1.getText().toString().equals(""))) {
                        height = Integer.parseInt(heightNum1.getText().toString());
                    }
                    if (!(height2.getText().toString().equals(""))) {
                        inch = Integer.parseInt(height2.getText().toString());

                    }
                    if (weight != 0 && height != 0 && inch != 0)
                        bmi = (weight * 703) / (((height * 12) + inch) * ((height * 12) + inch));
                }
                bmiprime = bmi / 25;
                String bmiInterpretation = interpretBMI(bmi, bmiprime);
                Bmi.setText(String.format("%.2f", bmi));
                PrimeBmi.setText(String.format("%.2f", bmiprime));
                resultLabel.setText(bmiInterpretation + "\nHealthy Weight Range = BMI between 18.5 and 24.9");
                if (Bmi != null) {
                    Chart.setVisibility(View.VISIBLE);
                }
            }
        });


        return rootView;
    }

    // interpret what BMI means
    private String interpretBMI(double bmi, double bmiprime) {

        if (bmi < 16 && bmiprime < 0.60) {
            return "You are very severely Underweight";
        } else if (bmi < 18.5 && bmiprime < 0.74) {
            return "You are Underweight";
        } else if (bmi < 25 && bmiprime < 1.0) {
            return "You are Healthy";
        } else if (bmi < 30 && bmiprime < 1.2) {
            return "You are Overweight";
        } else if (bmi < 40 && bmiprime < 1.6) {
            return "You are Moderately  Obese";
        } else if (bmi >= 40 && bmiprime >= 1.6) {
            return "You are very morbid Obese";
        } else {
            return "Enter your Details";
        }
    }

    //spinner listener for values male and female
    public class SpinnerListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            units = parent.getItemAtPosition(pos).toString();
            if (units.contains("Kg/cm")) {
                height2.setVisibility(View.GONE);
                weightNum.setHint(getString(R.string.weightR));
                heightNum1.setHint(getString(R.string.heightR));
            } else if (units.contains("lbs/in")) {
                height2.setVisibility(View.VISIBLE);
                height2.setHint("Inch");
                weightNum.setHint("Weight in Pounds(lbs)");
                heightNum1.setHint("Foot");
            }
        }


        public void onItemSelected1(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    }
}
