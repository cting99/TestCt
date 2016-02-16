package com.test.ct;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class ViewTest extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!Main.processIntent(this)){
			setContentView(R.layout.text_align_view);
		}

		
		
	}
}
