package com.hlibrary.net.parse

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.hlibrary.net.callback.IParseCallback
import com.hlibrary.net.callback.IParseConfig
import com.hlibrary.net.model.Respond
import com.hlibrary.net.util.Constants
import com.hlibrary.net.util.Constants.debug
import com.hlibrary.util.Logger
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method

class CommonParse : IParseCallback {

    var context: Context? = null
    var keyCode: String? = null
    var keyErrorMsg: String? = null
    var keyCodeSuc: String? = null
    var keyData: String? = null
    var keyArrayData: String? = null

    companion object {
        private var instance: CommonParse? = null
        @JvmStatic
        fun getInstance(context: Context): CommonParse {
            if (instance == null) {
                synchronized(CommonParse::class) {
                    if (instance == null) {
                        instance = CommonParse(context)
                    }
                }
            }
            return instance!!
        }
    }

    private constructor(context: Context) {
        this.context = context?.applicationContext
    }

    private fun initCode() {
        var application: Application = context as Application
        if (application is IParseConfig) {
            keyCode = application.getNetResponseCodeKey()
            keyCodeSuc = application.getNetResponseCodeSuc()
        } else {
            try {
                var cls = application.javaClass
                var method: Method = cls.getDeclaredMethod(Constants.NET_RESPONSE_CODE_KEY)
                keyCode = method.invoke(application) as String
                method = cls.getDeclaredMethod(Constants.NET_RESPONSE_CODE_SUC)
                keyCodeSuc = method.invoke(application) as String

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initErrorMsg() {
        var application: Application = context as Application
        if (application is IParseConfig) {
            keyErrorMsg = application.getNetResponseErrorMsgKey()
        } else {
            try {
                var cls = application.javaClass
                var method: Method = cls.getDeclaredMethod(Constants.NET_RESPONSE_ERROR_MSG)
                keyErrorMsg = method.invoke(application) as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initData() {
        var application: Application = context as Application
        if (application is IParseConfig) {
            keyData = application.getNetResponseData()
        } else {
            try {
                var cls = application.javaClass
                var method: Method = cls.getDeclaredMethod(Constants.NET_RESPONSE_DATA)
                keyData = method.invoke(application) as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initArrayData() {
        var application: Application = context as Application
        if (application is IParseConfig) {
            keyArrayData = application.getNetResponseArrayData()
        } else {
            try {
                var cls = application.javaClass
                var method: Method = cls.getDeclaredMethod(Constants.NET_RESPONSE_ARRAY_DATA)
                keyArrayData = method.invoke(application) as String
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun isValidRespond(respond: Respond?): Boolean {
        if (debug)
            Logger.getInstance().defaultTagD(respond)
        if (respond?.code == Respond.SUCCEE) {
            try {
                var dataObj = JSONObject(respond.getData())
                if (TextUtils.isEmpty(keyCode) || TextUtils.isEmpty(keyCodeSuc))
                    initCode()
                var codeJSON: JSONObject? = null
                var code = ""
                keyCode?.split(Constants.NET_RESPONSE_SEPARATE)?.forEach {
                    if (codeJSON == null) {
                        codeJSON = dataObj.optJSONObject(it)
                        if (codeJSON == null) {
                            code = dataObj.optString(it, null)
                            return@forEach
                        }
                    } else {
                        var tempJSON = codeJSON?.optJSONObject(it)
                        if (tempJSON != null)
                            codeJSON = tempJSON
                        else {
                            code = codeJSON?.optString(it, null)!!
                            return@forEach
                        }
                    }
                }

                if (keyCodeSuc.equals(code))
                    return true


            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return false
    }

    override fun errorNotice(respond: Respond?): String {

        if (TextUtils.isEmpty(keyErrorMsg)) {
            initErrorMsg()
        }

        var errorMsgJSON: JSONObject? = null
        try {
            var dataObj = JSONObject(respond?.getData())
            keyErrorMsg?.split(Constants.NET_RESPONSE_SEPARATE)?.forEach {
                if (errorMsgJSON == null) {
                    errorMsgJSON = dataObj.optJSONObject(it)
                    if (errorMsgJSON == null) {
                        return dataObj.optString(it)
                    }
                } else {
                    var tempObj = errorMsgJSON?.optJSONObject(it)
                    if (tempObj == null) {
                        return errorMsgJSON?.optString(it)!!
                    } else {
                        errorMsgJSON = tempObj
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun getObjectString(respond: Respond?): String {
        if (TextUtils.isEmpty(keyData)) {
            initData()
        }



        try {
            val dataObj = JSONObject(respond?.getData())
            if (debug)
                Logger.getInstance().defaultTagD(dataObj?.toString())
            var objJson: JSONObject? = null
            keyData?.split(Constants.NET_RESPONSE_SEPARATE)?.forEach {
                if (objJson == null) {
                    objJson = dataObj.optJSONObject(it)
                    if (objJson == null)
                        return dataObj.optString(it, "{}")
                } else {
                    var tempObj = objJson?.optJSONObject(it)
                    if (tempObj == null) {
                        return objJson?.optString(it, "{}")!!
                    }
                    objJson = tempObj
                }
            }
            if (objJson != null)
                return objJson.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return respond?.data!!

    }

    override fun getArrayString(respond: Respond?): String {
        if (TextUtils.isEmpty(keyArrayData)) {
            initArrayData()
        }

        try {
            var dataObj = JSONObject(respond?.getData())
            var objJson: JSONObject? = null
            keyArrayData?.split(Constants.NET_RESPONSE_SEPARATE)?.forEach {
                if (objJson == null) {
                    objJson = dataObj.optJSONObject(it)
                    if (objJson == null)
                        return dataObj.optString(it, "[]")
                } else {
                    var tempObj = objJson?.optJSONObject(it)
                    if (tempObj == null)
                        return objJson?.optString(it, "[]")!!
                    objJson = tempObj
                }
            }
            if (objJson != null)
                return objJson.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return respond?.data!!
    }

}