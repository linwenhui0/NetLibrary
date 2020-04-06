package com.hlibrary.net.callback;

import com.hlibrary.net.model.Respond;

public interface IParseCallback {
    boolean isValidRespond(Respond respond);

    String errorNotice(Respond respond);

    String getObjectString(Respond respond);

    String getArrayString(Respond respond);

//    protected boolean isValidRespond(Respond respond) {
//        Logger.i(TAG, JSON.toJSONString(respond));
//        if (respond.getCode() == Respond.Succee) {
//            try {
//                Logger.i(TAG, respond.getData());
//                JSONObject dataObj = new JSONObject(respond.getData());
//                JSONObject msgObj = dataObj.getJSONObject("msg");
//                int code = msgObj.optInt("code", -1);
//                if (code == 0) {
//                    return true;
//                }
//                httpConfig.setErrorNotice(msgObj.optString("desc"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return false;
//    }
//
//    protected T getObjectString(Respond respond, Class<T> cls) {
//
//            try {
//                JSONObject jsonObject = new JSONObject(respond.getData());
//                return jsonObject.getString("content");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        return null;
//    }
//
//    protected List<T> getArrayString(Respond respond, Class<T> cls) {
//        if (isValidRespond(respond)) {
//            try {
//                JSONObject jsonObject = new JSONObject(respond.getData());
//                JSONObject data = jsonObject.getJSONObject("content");
//                Logger.i(TAG, data.toString());
//                return data.getJSONArray("list");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }
}
