package com.test.ct;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.test.ct.R;
import com.test.ct.sky.Aquarius;

/**
 * Created by cting on 2016/2/3.
 */
public class AquariusTest extends Activity implements Switch.OnCheckedChangeListener, View.OnClickListener{

    Aquarius mBatteryView;

    CheckBox mPaintStyle;
    CheckBox mClipCk;
    CheckBox mAnimCk;

    EditText mWaveHeightRatio;
    EditText mWaveDuration;
    EditText mWaveCycles;
    EditText mWaterLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aquarius);

        mBatteryView=(Aquarius)findViewById(R.id.aqurius);
        mPaintStyle= (CheckBox) findViewById(R.id.paint_style);
        mClipCk= (CheckBox) findViewById(R.id.clip_circle);
        mAnimCk= (CheckBox) findViewById(R.id.anim);
        mWaveHeightRatio = (EditText) findViewById(R.id.wave_height);
        mWaveDuration = (EditText) findViewById(R.id.wave_duration);
        mWaveCycles = (EditText) findViewById(R.id.wave_cycles);
        mWaterLevel = (EditText) findViewById(R.id.water_level);

        mPaintStyle.setChecked(mBatteryView.isPaintFill());
        mPaintStyle.setOnClickListener(this);

        mClipCk.setChecked(mBatteryView.isClipped());
        mClipCk.setOnClickListener(this);

        mAnimCk.setChecked(true);
        mAnimCk.setOnClickListener(this);

        mWaveHeightRatio.setText(mBatteryView.getWaveHeightRatio() + "");
        mWaveDuration.setText(mBatteryView.getWaveDuration() + "");
        mWaveCycles.setText(mBatteryView.getWaveCycles() + "");
        mWaterLevel.setText(mBatteryView.getWaterLevelPercent() + "");

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView==mPaintStyle){
            mBatteryView.setPaintFill(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.set){
            float waveHeightRatio=Float.parseFloat(mWaveHeightRatio.getText().toString());
            mBatteryView.setWaveHeightRatio(waveHeightRatio);

            long waveDuration=Long.parseLong(mWaveDuration.getText().toString());
            mBatteryView.setWaveDuration(waveDuration);

            float waveCycles = Float.parseFloat(mWaveCycles.getText().toString());
            mBatteryView.setWaveCycles(waveCycles);

            int waterLevel=Integer.parseInt(mWaterLevel.getText().toString());
            mBatteryView.setWaterLevelPercent(waterLevel);

            mBatteryView.reset();

        }else if(id==R.id.paint_style){
            mBatteryView.setPaintFill(mPaintStyle.isChecked());

        }else if(id==R.id.clip_circle){
            mBatteryView.setClipped(mClipCk.isChecked());

        }else if(id==R.id.anim){
            mBatteryView.setAnim(mAnimCk.isChecked());

        }



    }
}
