/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.cameraui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter.I_ManualParameterEvent;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.sonyremote.parameters.manual.BaseManualParameterSony;
import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout implements I_ManualParameterEvent
{

    private final String TAG = ManualButton.class.getSimpleName();
    private String[] parameterValues;
    private ManualParameterInterface parameter;
    private TextView valueTextView;
    private ImageView imageView;
    private Handler handler;
    private final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    private final int backgroundColor = Color.parseColor("#00000000");
    private final int stringColor = Color.parseColor("#FFFFFFFF");
    private final int stringColorActive = Color.parseColor("#FF000000");
    private int pos;
    protected ActivityInterface fragment_activityInterface;
    private AppSettingsManager.SettingMode settingMode;

    private final BlockingQueue<Integer> valueQueue = new ArrayBlockingQueue<>(3);

    public ManualButton(Context context, AppSettingsManager.SettingMode settingMode, ManualParameterInterface parameter, int drawableImg)
    {
        super(context);
        init(context);
        this.settingMode = settingMode;
        SetManualParameter(parameter);
        imageView.setImageDrawable(getResources().getDrawable(drawableImg));
    }

    private void init(Context context)
    {
        handler = new Handler();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.cameraui_manualbutton, this);
        valueTextView = (TextView) findViewById(id.manualbutton_valuetext);
        valueTextView.setSelected(true);
        imageView = (ImageView) findViewById(id.imageView_ManualButton);
    }

    public void RemoveParameterListner( I_ManualParameterEvent t)
    {
        parameter.removeEventListner(t);
    }

    public void SetParameterListner( I_ManualParameterEvent t)
    {
        parameter.addEventListner(t);
    }

    public void SetManualParameter(@Nullable ManualParameterInterface parameter)
    {
        this.parameter = parameter;
        if (parameter != null) {
            parameter.addEventListner(this);
            if (parameter.IsSupported())
            {
                String txt = parameter.GetStringValue();
                if (valueTextView != null) {
                    if (txt != null && !txt.equals(""))
                        valueTextView.setText(txt);
                    else
                        valueTextView.setText(parameter.GetValue() + "");
                }

                onIsSupportedChanged(parameter.IsVisible());
                onIsSetSupportedChanged(parameter.IsSetSupported());
                createStringParametersStrings(parameter);

            }
            else
                onIsSupportedChanged(false);
        }
        else
            onIsSupportedChanged(false);

    }

    private void createStringParametersStrings(ManualParameterInterface parameter) {
        parameterValues = parameter.getStringValues();
    }



    @Override
    public void onIsSupportedChanged(final boolean value) {
        post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    setVisibility(View.VISIBLE);
                    animate().setListener(null).scaleX(1f).setDuration(300);
                }
                else
                {
                    animate().setListener(hideListner).scaleX(0f).setDuration(300);
                }
            }
        });
    }

    private final AnimatorListener hideListner = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onIsSetSupportedChanged(final boolean value) {
        post(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    setEnabled(true);
                    imageView.getDrawable().setColorFilter(Color.TRANSPARENT, Mode.SRC_ATOP);
                } else {
                    setEnabled(false);
                    imageView.getDrawable().setColorFilter(Color.GRAY, Mode.SRC_ATOP);
                }
            }
        });
    }


    @Override
    public void onCurrentValueChanged(int current)
    {

        pos = current;

        Log.d(TAG, "onCurrentValueChanged current:"+current +" pos:" + pos);
        setTextValue(current);
    }

    @Override
    public void onValuesChanged(String[] values) {
        parameterValues = values;
    }

    @Override
    public void onCurrentStringValueChanged(final String value) {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                valueTextView.setText(value);
            }
        });
    }

    private void setTextValue(final int current)
    {
        valueTextView.post(new Runnable() {
            @Override
            public void run() {
                String txt = getStringValue(current);
                if (txt != null && !txt.equals("") && !txt.equals("null"))
                    valueTextView.setText(txt);
                else
                    valueTextView.setText(current+"");
                Log.d(TAG, "setTextValue:" + valueTextView.getText());
            }
        });

    }

    private String getStringValue(int pos)
    {
        if (parameterValues != null && parameterValues.length > 0)
        {
            if (pos >= parameterValues.length)
                return parameterValues[parameterValues.length-1];
            else if (pos < 0)
                return parameterValues[0];
            else
                return parameterValues[pos];
        }

        return null;
    }

    public String[] getStringValues()
    {
        if (parameterValues == null)
            createStringParametersStrings(parameter);
        return parameterValues;
    }

    public int getCurrentItem()
    {
        return parameter.GetValue();
    }

    public void setValueToParameters(int value)
    {
        if (valueQueue.size() == 3)
            valueQueue.remove();
        //Log.d(TAG, "add to queue:" + value);
        valueQueue.add(value);

        handler.post(new Runnable() {
            @Override
            public void run() {
                //setparameter();
                while (valueQueue.size() >= 1) {
                    setparameter();

                }

            }
        });


    }

    private void setparameter()
    {
        boolean currentlysettingsparameter = true;
        int runValue = 0;
        try {
            runValue = valueQueue.take();

        } catch (InterruptedException e) {
            Log.WriteEx(e);
            currentlysettingsparameter = false;
        }
        pos = runValue;
        if (runValue < 0 || runValue > parameterValues.length -1)
            return;
        parameter.SetValue(runValue);
        if (!(parameter instanceof BaseManualParameterSony)) {
            settingMode.set(String.valueOf(runValue));
        }
        currentlysettingsparameter = false;
    }

    public void SetActive(boolean active) {
        if (active) {
            setBackgroundColor(backgroundColorActive);
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

}
