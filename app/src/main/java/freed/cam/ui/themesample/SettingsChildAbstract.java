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

package freed.cam.ui.themesample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.utils.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 16.06.2016.
 */
public abstract class SettingsChildAbstract extends LinearLayout implements SettingsChildInterface
{

    public interface SettingsChildClick
    {
        void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment);
    }

    public interface CloseChildClick
    {
        void onCloseClicked(String value);
    }

    protected ModeParameterInterface parameter;
    protected ActivityInterface fragment_activityInterface;
    protected AppSettingsManager.SettingMode settingMode;
    protected String key_appsettings;
    protected TextView valueText;

    protected SettingsChildClick onItemClick;
    protected boolean fromleft;

    public SettingsChildAbstract(Context context, AppSettingsManager.SettingMode settingsMode, ModeParameterInterface parameter)
    {
        super(context);
        this.settingMode = settingsMode;
        this.parameter = parameter;
        if (parameter == null)
            return;
        String value = parameter.GetValue();
        onParameterValueChanged(value);
    }


    @Override
    public void SetStuff(ActivityInterface fragment_activityInterface, String settingvalue)
    {
        this.fragment_activityInterface = fragment_activityInterface;
        key_appsettings = settingvalue;
    }

    @Override
    public void SetStuff(AppSettingsManager.SettingMode settingMode) {
        this.settingMode = settingMode;
        String value = settingMode.get();
        if (value.equals("") || value == null)
            value = settingMode.getValues()[0];
        onParameterValueChanged(value);
    }

    public SettingsChildAbstract(Context context) {
        super(context);
    }

    public SettingsChildAbstract(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract void sendLog(String log);

    protected abstract void init(Context context);
    protected abstract void inflateTheme(LayoutInflater inflater);

    public void SetMenuItemClickListner(SettingsChildClick menuItemClick, boolean fromleft)
    {
        onItemClick = menuItemClick;
        this.fromleft = fromleft;
    }

    public void SetUiItemClickListner(SettingsChildClick menuItemClick)
    {
        onItemClick = menuItemClick;
    }

    @Override
    public void SetParameter(ModeParameterInterface parameter) {
        if (parameter == null || !parameter.IsSupported())
        {
            onParameterIsSupportedChanged(false);
            sendLog("Paramters is null or Unsupported");
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
            return;
        }
        else
        {
            onParameterIsSupportedChanged(parameter.IsVisible());
            if (parameter != null) {
                parameter.addEventListner(this);
                this.parameter = parameter;
            }
        }
    }

    @Override
    public ModeParameterInterface GetParameter()
    {
        return parameter;
    }

    @Override
    public String[] GetValues()
    {
        if (parameter != null && parameter.IsSupported())
            return parameter.GetValues();
        else return null;
    }

    @Override
    public void SetValue(String value)
    {
        if (parameter != null && parameter.IsSupported())
        {
            if (key_appsettings != null && !key_appsettings.equals(""))
                fragment_activityInterface.getAppSettings().setApiString(key_appsettings, value);
            if (settingMode != null)
                settingMode.set(value);
            try {
                parameter.SetValue(value, true);
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }
            onParameterValueChanged(value);
        }
    }
}
