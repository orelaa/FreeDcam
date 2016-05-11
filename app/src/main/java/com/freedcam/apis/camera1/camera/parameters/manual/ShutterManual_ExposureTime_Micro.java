package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends BaseManualParameter
{
    private final String TAG = ShutterManual_ExposureTime_Micro.class.getSimpleName();
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_Micro(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues, String value, String maxval , String minval ) {
        super(parameters, value, maxval, minval, camParametersHandler,1);
        try {
            if (shuttervalues == null)
            {
                Logger.d(TAG, "minexpo = "+parameters.get(min_value) + " maxexpo = " + parameters.get(max_value));
                if (!parameters.get(min_value).contains("."))
                {
                    Logger.d(TAG, "Micro does not contain .  load int");
                    int min = Integer.parseInt(parameters.get(min_value));
                    int max = Integer.parseInt(parameters.get(max_value));
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }
                else
                {
                    Logger.d(TAG, "Micro contain .  *100000");
                    double tmpMin = Double.parseDouble(parameters.get(min_value))*1000000;
                    double tmpMax = Double.parseDouble(parameters.get(max_value))*1000000;
                    int min = (int)tmpMin;
                    int max = (int)tmpMax;
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }

            }
            else
                stringvalues = shuttervalues;
            parameters.put(value, "0");
            this.isSupported = true;

        } catch (NumberFormatException ex) {
            Logger.exception(ex);
            isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals("Auto"))
        {
            String shutterstring = StringUtils.FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = StringUtils.getMicroSec(shutterstring);
            Logger.d(TAG, " StringUtils.getMicroSec"+ shutterstring);
            parameters.put(value, shutterstring);
        }
        else
        {
            parameters.put(value, "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
