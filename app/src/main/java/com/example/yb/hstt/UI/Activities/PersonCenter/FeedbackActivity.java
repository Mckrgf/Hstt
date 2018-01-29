package com.example.yb.hstt.UI.Activities.PersonCenter;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yb.hstt.Base.HsttBaseActivity;
import com.example.yb.hstt.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends HsttBaseActivity implements View.OnClickListener {

    @BindView(R.id.tb_feedback)
    Toolbar tbFeedback;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.divide_a)
    View divideA;
    @BindView(R.id.rb_feedback_a)
    RadioButton rbFeedbackA;
    @BindView(R.id.rb_feedback_b)
    RadioButton rbFeedbackB;
    @BindView(R.id.rb_feedback_c)
    RadioButton rbFeedbackC;
    @BindView(R.id.rg_feedback)
    RadioGroup rgFeedback;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.et_change_psd)
    Button etChangePsd;
    @BindView(R.id.activity_feedback)
    RelativeLayout activityFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        tbFeedback.setNavigationIcon(R.mipmap.ic_return);
        tbFeedback.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case -1:
                finish();
                break;
        }
    }
}
