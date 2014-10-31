/*
 * Copyright (C) 2013 XuiMod
 * Copyright (C) 2012 The CyanogenMod Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zst.app.multiwindowsidebar.preference;


import com.zst.app.multiwindowsidebar.R;
import com.zst.app.multiwindowsidebar.Util;
import com.zst.app.multiwindowsidebar.preference.colorpicker.ColorSettingsDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ColorPickerDialog extends Preference implements OnPreferenceClickListener {
	
	SharedPreferences mPref;
	ImageView mColorBox;
	Resources mRes;
	String mDefaultColor;
	boolean mHideAlpha;

	public ColorPickerDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDefaultColor = attrs.getAttributeValue(null, "defaultValue");
		if (attrs.getAttributeValue(null, "hideAlpha") != null) {
			mHideAlpha = true;
		}
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);		
		mColorBox = (ImageView) view.findViewById(android.R.id.icon);
		mPref = getPreferenceManager().getSharedPreferences();
		mRes = getContext().getResources();
		setOnPreferenceClickListener(this);
		refreshColorBox();
	}

	@Override
	public boolean onPreferenceClick(Preference arg0) {
		final ColorSettingsDialog d = new ColorSettingsDialog(getContext(), getColor(), mDefaultColor);
		d.setAlphaSliderVisible(!mHideAlpha);
		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case AlertDialog.BUTTON_POSITIVE:
					setColor(d.getColorString());
					break;
				case AlertDialog.BUTTON_NEUTRAL:
					setColor(mDefaultColor);
					break;
				}
			}
		};
		d.setButton(AlertDialog.BUTTON_POSITIVE, mRes.getString(android.R.string.ok), listener);
		d.setButton(AlertDialog.BUTTON_NEUTRAL, mRes.getString(R.string.settings_default), listener);
		d.setButton(AlertDialog.BUTTON_NEGATIVE, mRes.getString(android.R.string.cancel), listener);
		d.show();
		return true;
	}
	
	public String getColorString() {
		return Integer.toHexString(getColor());
	}
	
	public int getColor() {
		String str = mPref.getString(getKey(), mDefaultColor);
		return Util.parseColorFromString(str, mDefaultColor);
	}
	
	private void refreshColorBox(){
		if (mColorBox != null) {
			mColorBox.setBackgroundColor(getColor());
			mColorBox.setVisibility(View.VISIBLE);
		}
	}
	public void setColor(String clr) {
		mPref.edit().putString(getKey(), clr).commit();
		refreshColorBox();
		if (getOnPreferenceChangeListener() != null) {
			getOnPreferenceChangeListener().onPreferenceChange(this, clr);
		}
	}
}