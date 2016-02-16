package com.test.ct;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.test.ct.R;
import com.test.ct.sky.Aquarius;
import com.test.ct.sky.SetValueView;

/**
 * Created by cting on 2016/2/3.
 */
public class AquariusTest extends Activity implements Switch.OnCheckedChangeListener, View.OnClickListener{

    Switch mPaintStyle;
    SetValueView mWaveHeightSet;
    SetValueView mWaveSet;
    SetValueView mWaterLevelPercent;
    SetValueView mCyclesSet;
    Aquarius mBatteryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aquarius);

        mPaintStyle= (Switch) findViewById(R.id.paint_style);
        mWaveHeightSet= (SetValueView) findViewById(R.id.wave_height_set);
        mWaveSet= (SetValueView) findViewById(R.id.wave_set);
        mWaterLevelPercent = (SetValueView) findViewById(R.id.water_level_set);
        mCyclesSet= (SetValueView) findViewById(R.id.cycles_set);

        mBatteryView=(Aquarius)findViewById(R.id.aqurius);

        mPaintStyle.setOnCheckedChangeListener(this);
        mPaintStyle.setChecked(false);

        mWaveHeightSet.init(R.string.ac_aquarius_wave_height_ratio, String.valueOf(8), this);
        mWaveSet.init(R.string.ac_aquarius_wave_duration, String.valueOf(8000), this);
        mWaterLevelPercent.init(R.string.ac_aquarius_water_level, String.valueOf(50), this);
        mCyclesSet.init(R.string.ac_aquarius_wave_cycles, String.valueOf(1.0), this);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView==mPaintStyle){
            mBatteryView.setPaintFill(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        if(mWaveHeightSet.isClicked(v)){
            long waveRatio= Long.parseLong(mWaveHeightSet.getValue());
            mBatteryView.setWaveRatio(waveRatio);
        }else if(mWaveSet.isClicked(v)){
            long waveDuration= Long.parseLong(mWaveSet.getValue());
            mBatteryView.setWaveDuration(waveDuration);
        }else if(mWaterLevelPercent.isClicked(v)){
            int percent = Integer.parseInt(mWaterLevelPercent.getValue());
            mBatteryView.setWaterLevelPercent(percent);
        }else if(mCyclesSet.isClicked(v)){
            float cycles = Float.parseFloat(mCyclesSet.getValue());
            mBatteryView.setCycles(cycles);
        }
    }
}
