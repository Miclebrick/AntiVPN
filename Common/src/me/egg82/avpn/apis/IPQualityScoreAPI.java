package me.egg82.avpn.apis;

import java.util.Optional;

import org.json.simple.JSONObject;

import me.egg82.avpn.Configuration;
import ninja.egg82.patterns.ServiceLocator;
import ninja.egg82.plugin.utils.WebUtil;

public class IPQualityScoreAPI implements IFetchAPI {
    // vars

    // constructor
    public IPQualityScoreAPI() {

    }

    // public
    public String getName() {
        return "ipqualityscore";
    }

    public Optional<Boolean> getResult(String ip) {
        String key = ServiceLocator.getService(Configuration.class).getNode("sources", "ipqualityscore", "key").getString();
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }

        JSONObject json = null;
        try {
            json = WebUtil.getJsonObject("http://www.ipqualityscore.com/api/json/ip/" + key + "/" + ip, "egg82/AntiVPN");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
        if (json == null) {
            return Optional.empty();
        }

        if (!((Boolean) json.get("success")).booleanValue()) {
            return Optional.empty();
        }

        if (((Boolean) json.get("proxy")).booleanValue()) {
            return Optional.of(Boolean.TRUE);
        }
        if (((Boolean) json.get("vpn")).booleanValue()) {
            return Optional.of(Boolean.TRUE);
        }
        if (((Boolean) json.get("tor")).booleanValue()) {
            return Optional.of(Boolean.TRUE);
        }
        if (((Boolean) json.get("is_crawler")).booleanValue()) {
            return Optional.of(Boolean.TRUE);
        }

        double retVal = ((Number) json.get("fraud_score")).doubleValue();
        if (retVal < 0.0d) {
            return Optional.empty();
        }

        return Optional.of((retVal >= ServiceLocator.getService(Configuration.class).getNode("sources", "ipqualityscore", "threshold").getDouble()) ? Boolean.TRUE : Boolean.FALSE);
    }

    // private

}
