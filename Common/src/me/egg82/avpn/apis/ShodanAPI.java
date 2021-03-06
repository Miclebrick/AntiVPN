package me.egg82.avpn.apis;

import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import me.egg82.avpn.Configuration;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.WebUtil;

public class ShodanAPI implements IFetchAPI {
    // vars

    // constructor
    public ShodanAPI() {

    }

    // public
    public String getName() {
        return "shodan";
    }

    public Optional<Boolean> getResult(String ip) {
        String key = ServiceLocator.getService(Configuration.class).getNode("sources", "shodan", "key").getString();
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }

        JSONObject json = null;
        try {
            json = WebUtil.getJsonObject("https://api.shodan.io/shodan/host/" + ip + "?key=" + key, "egg82/AntiVPN");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
        if (json == null) {
            return Optional.empty();
        }

        JSONArray tags = (JSONArray) json.get("tags");
        if (tags == null) {
            return Optional.empty();
        }

        if (tags.size() == 0) {
            return Optional.of(Boolean.FALSE);
        }
        for (Object tag : tags) {
            String t = (String) tag;
            if (t.equalsIgnoreCase("vpn")) {
                return Optional.of(Boolean.TRUE);
            }
        }

        return Optional.of(Boolean.FALSE);
    }

    // private

}
