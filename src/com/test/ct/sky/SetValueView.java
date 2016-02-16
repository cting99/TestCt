package com.test.ct.sky;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.test.ct.R;

/**
 * Created by cting on 2016/2/3.
 */
public class SetValueView extends LinearLayout implements TextWatcher{

    TextView mLable;
    EditText mText;
    Button mSetBtn;

    public SetValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLable=(TextView)findViewById(R.id.label);
        mText=(EditText)findViewById(R.id.edit);
        mSetBtn=(Button)findViewById(R.id.btn_set);
    }

    public void init(int lableId,String defValue,View.OnClickListener clickListener){
        onSet(clickListener);
        setLable(lableId);
        setTextHint(defValue);
    }

    private void setLable(int lableId){
        mLable.setText(lableId);
    }
    private void setTextHint(String hint){
        mText.setHint(hint);
        mText.setText("");
    }
    private void setText(String text){
        mText.setText(text);
        mText.setHint(text);
    }
    private void onSet(View.OnClickListener clickListener){
        mSetBtn.setOnClickListener(clickListener);
    }

    public boolean isClicked(View v){
        return v==mSetBtn && v!=null;
    }
    public String getValue(){
        return mText.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mSetBtn.setEnabled(!TextUtils.isEmpty(s.toString()));

    }
}
