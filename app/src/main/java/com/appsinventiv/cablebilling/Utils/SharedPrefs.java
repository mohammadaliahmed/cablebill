package com.appsinventiv.cablebilling.Utils;

import android.content.Context;
import android.content.SharedPreferences;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.saffron.club.Models.UserModel;

import com.appsinventiv.cablebilling.Models.AgentModel;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.google.gson.Gson;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }


//    public static void setMessagesList(String username, ArrayList<ChatModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter(username + "messages", json);
//    }
//
//    public static ArrayList getMessagesList(String username) {
//        Gson gson = new Gson();
//        ArrayList<ChatModel> playersList = (ArrayList<ChatModel>) gson.fromJson(preferenceGetter(username + "messages"),
//                new TypeToken<ArrayList<ChatModel>>() {
//                }.getType());
//        return playersList;
//    }
//


    //    public static void setCartMenuIds(HashMap<Integer, Integer> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("cartMenu", json);
//    }
//
//    public static HashMap<Integer, Integer> getCartMenuIds() {
//        Gson gson = new Gson();
//
//        HashMap<Integer, Integer> retMap = new Gson().fromJson(
//                preferenceGetter("cartMenu"), new TypeToken<HashMap<Integer, Integer>>() {
//                }.getType()
//        );
//
//        return retMap;
//    }
//
//    public static void clearCartMenuIds() {
//        Gson gson = new Gson();
//        String json = "";
//        preferenceSetter("cartMenu", json);
//
//
//    }
//
//    public static void setTableIds(HashMap<Integer, Integer> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("tableId", json);
//    }
//
//    public static HashMap<Integer, Integer> getTableIds() {
//        Gson gson = new Gson();
//
//        HashMap<Integer, Integer> retMap = new Gson().fromJson(
//                preferenceGetter("tableId"), new TypeToken<HashMap<Integer, Integer>>() {
//                }.getType()
//        );
//
//        return retMap;
//    }
//
//    public static void clearTableIds() {
//        Gson gson = new Gson();
//        String json = "";
//        preferenceSetter("tableId", json);
//
//
//    }
//
    public static void setLoggedInAs(String token) {
        preferenceSetter("setLoggedIn", token);
    }

    public static String getLoggedInAs() {
        return preferenceGetter("setLoggedIn");
    }

    public static void setLoggedInAsWhichAdmin(String token) {
        preferenceSetter("setLoggedInAsWhichAdmin", token);
    }

    public static String getLoggedInAsWhichAdmin() {
        return preferenceGetter("setLoggedInAsWhichAdmin");
    }

    public static void setParchiName(String token) {
        preferenceSetter("setParchiName", token);
    }

    public static String getParchiName() {
        return preferenceGetter("setParchiName");
    }

    public static void setTitle(String token) {
        preferenceSetter("getTitle", token);
    }

    public static String getTitle() {
        return preferenceGetter("getTitle");
    }

    public static void setAddress(String token) {
        preferenceSetter("setAddress", token);
    }

    public static String getAddress() {
        return preferenceGetter("setAddress");
    }

    public static void setLogoUrl(String token) {
        preferenceSetter("setLogoUrl", token);
    }

    public static String getLogoUrl() {
        return preferenceGetter("setLogoUrl");
    }

    //
//
//    public static UserModel getParticipantModel(String userId) {
//        Gson gson = new Gson();
//        UserModel model = gson.fromJson(preferenceGetter(userId), UserModel.class);
//        return model;
//    }
//
//
    public static void setAgent(AgentModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("setAgent", json);
    }

    public static AgentModel getAgent() {
        Gson gson = new Gson();
        AgentModel model = gson.fromJson(preferenceGetter("setAgent"), AgentModel.class);
        return model;
    }


    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }


    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

}
