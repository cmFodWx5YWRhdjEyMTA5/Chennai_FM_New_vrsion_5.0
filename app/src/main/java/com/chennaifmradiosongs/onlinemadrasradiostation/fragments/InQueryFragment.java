package com.chennaifmradiosongs.onlinemadrasradiostation.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chennaifmradiosongs.onlinemadrasradiostation.R;
import com.chennaifmradiosongs.onlinemadrasradiostation.SendMail;

public class InQueryFragment extends Fragment {

    private EditText firstname, radio_name,radio_url, email, mobile_no, description;
    private Button submitform;

    public static void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = view.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_addradioform
                , container, false);

        firstname = rootView.findViewById(R.id.firstname);
        radio_name = rootView.findViewById(R.id.radio_name);
        radio_url = rootView.findViewById(R.id.radio_url);
        email = rootView.findViewById(R.id.email);
        mobile_no = rootView.findViewById(R.id.mobile_no);
        description = rootView.findViewById(R.id.description);

        submitform = rootView.findViewById(R.id.submitform);

        submitform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Configure Query
                final String firstname1 = firstname.getText().toString().trim();
                final String radio_name1 = radio_name.getText().toString().trim();
                final String radio_url1 = radio_url.getText().toString().trim();
                final String email1 = email.getText().toString().trim();

                final String mobile_no1 = mobile_no.getText().toString().trim();
                final String description1 = description.getText().toString().trim();

                if (TextUtils.isEmpty(firstname1)) {
                    firstname.setError("Enter First Name!");
                    return;
                }
                if (TextUtils.isEmpty(radio_name1)) {
                    radio_name.setError("Enter Radio Name!");
                    return;
                }
                if (TextUtils.isEmpty(radio_url1)) {
                    radio_url.setError("Enter Radio Name!");
                    return;
                }
                if (TextUtils.isEmpty(description1)) {
                    description.setError("Enter Description!");
                    return;
                }

                try {
                    new SendMail(getActivity(), getActivity().getResources().getString(R.string.email),
                            getActivity().getResources().getString(R.string.app_name)
                                    +" Online Radio Submit Form : " + firstname1
                            , "\n\n Name : " + firstname1  + "\n\n" +
                            "Email : " + email1 + "\n\n" +
                            "Mobile : " + mobile_no1 + "\n\n" +
                            "Radio Name : " + radio_name1 + "\n\n" +
                            "Radio URL : " + radio_url1 + "\n\n" +
                            "Message : " + description1).execute();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                // Saving object
                hideKeyboard(getActivity());

            }
        });
        return rootView;
    }

}
