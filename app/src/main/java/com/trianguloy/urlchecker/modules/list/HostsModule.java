package com.trianguloy.urlchecker.modules.list;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.trianguloy.urlchecker.R;
import com.trianguloy.urlchecker.activities.ConfigActivity;
import com.trianguloy.urlchecker.dialogs.MainDialog;
import com.trianguloy.urlchecker.modules.AModuleConfig;
import com.trianguloy.urlchecker.modules.AModuleData;
import com.trianguloy.urlchecker.modules.AModuleDialog;
import com.trianguloy.urlchecker.modules.companions.Hosts;
import com.trianguloy.urlchecker.url.UrlData;
import com.trianguloy.urlchecker.utilities.AndroidUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This module checks for patterns characters in the url
 */
public class HostsModule extends AModuleData {

    @Override
    public String getId() {
        return "hosts";
    }

    @Override
    public int getName() {
        return R.string.mHosts_name;
    }

    @Override
    public AModuleDialog getDialog(MainDialog cntx) {
        return new HostsDialog(cntx);
    }

    @Override
    public AModuleConfig getConfig(ConfigActivity cntx) {
        return new HostsConfig(cntx);
    }
}

class HostsConfig extends AModuleConfig {
    private final Hosts hosts;

    public HostsConfig(ConfigActivity cntx) {
        super(cntx);
        hosts = new Hosts(cntx);
    }

    @Override
    public boolean canBeEnabled() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.config_hosts;
    }

    @Override
    public void onInitialize(View views) {
        // click to build
        views.findViewById(R.id.rebuild).setOnClickListener(v ->
                hosts.build(false, () ->
                        Toast.makeText(getActivity(), getActivity().getString(R.string.mHosts_built, hosts.size()), Toast.LENGTH_LONG).show()
                )
        );
        // click to edit
        views.findViewById(R.id.edit).setOnClickListener(v ->
                hosts.showEditor()
        );
    }

}

class HostsDialog extends AModuleDialog {

    private final Hosts hosts;

    private TextView text;

    public HostsDialog(MainDialog dialog) {
        super(dialog);
        hosts = new Hosts(dialog);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_hosts;
    }

    @Override
    public void onInitialize(View views) {
        text = views.findViewById(R.id.text);
        text.setOnClickListener(o -> {
            if (hosts.isUninitialized()) hosts.build(true, () -> onNewUrl(getUrl()));
        });
    }

    @Override
    public void onNewUrl(UrlData urlData) {
        if (hosts.isUninitialized()) {
            // check built
            text.setText(R.string.mHosts_uninitialized);
            AndroidUtils.setRoundedColor(R.color.warning, text);
            return;
        }
        onNewUrl(urlData.url);
    }

    private void onNewUrl(String url) {

        // init
        String host;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            text.setText(R.string.mHosts_parseError);
            AndroidUtils.setRoundedColor(R.color.warning, text);
            return;
        }

        var label = hosts.contains(host);
        if (label != null) {
            text.setText(label.first);
            try {
                AndroidUtils.setRawRoundedColor(Color.parseColor(label.second), text);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                AndroidUtils.setRoundedColor(R.color.bad, text);
            }
        } else {
            text.setText(android.R.string.ok);
            AndroidUtils.clearRoundedColor(text);
        }
    }

}