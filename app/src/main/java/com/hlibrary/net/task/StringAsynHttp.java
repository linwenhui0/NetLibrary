package com.hlibrary.net.task;

import com.hlibrary.net.config.HttpConfig;
import com.hlibrary.net.config.HttpParamConfig;
import com.hlibrary.net.listener.IHttpAccessor;
import com.hlibrary.net.listener.parse.IParse;
import com.hlibrary.net.model.Respond;
import com.hlibrary.util.Logger;


/**
 * Created by linwenhui on 2015/10/28.
 */
public class StringAsynHttp extends NormalAsynHttp<String> {

    public StringAsynHttp(HttpConfig httpConfig) {
        super(httpConfig, String.class);
    }

    public StringAsynHttp(HttpConfig httpConfig, IHttpAccessor accessor) {
        super(httpConfig, accessor, String.class);
    }

    @Override
    protected String parse(Respond respond) {
        IParse parse = HttpParamConfig.getInstance().getParseFormat();
        if (parse.isValidRespond(respond)) {
            Logger.getInstance().i(" === parse === code = " + respond.getCode() + " = data = " + respond.getData());
            return HttpParamConfig.getInstance().getParseFormat().getObjectString(respond);
        }
        return null;
    }


}
