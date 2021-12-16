package com.eyflutter.eyflutter_cmi.bases;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/2/26
 * Description:
 * Modifier:
 * ModifyContent:
 */
public class BasicCompatActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            SuperActivitySupport.callUpActivity(this);
        }
    }

    protected AppCompatActivity getActivity() {
        return this;
    }
}
