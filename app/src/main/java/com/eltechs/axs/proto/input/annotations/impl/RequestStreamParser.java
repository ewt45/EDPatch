package com.eltechs.axs.proto.input.annotations.impl;

import android.util.Log;

import com.eltechs.axs.proto.input.XProtocolError;
import com.eltechs.axs.proto.input.parameterReaders.ParameterReader;
import com.eltechs.axs.xserver.XServer;

import java.util.Arrays;

/* loaded from: classes.dex */
public class RequestStreamParser {
    private final ParameterReader[] parameterReaders;

    /* JADX INFO: Access modifiers changed from: package-private */
     RequestStreamParser(ParameterReader[] parameterReaderArr) {
        this.parameterReaders = parameterReaderArr;
    }

    public Object[] parse(XServer xServer, Object obj, RequestDataRetrievalContext requestDataRetrievalContext) throws XProtocolError {
        ParametersCollectionContext paramsContext = new ParametersCollectionContext(obj, xServer, requestDataRetrievalContext, this.parameterReaders.length);
        for (ParameterReader parameterReader : this.parameterReaders) {
            parameterReader.readParameter(paramsContext);
        }
        if(paramsContext.getDataRetrievalContext().req.getMajorOpcode()==49){
            Log.e("TAG", "parse: 看看参数都读取了什么：参数列表 parameterReaders="+ Arrays.toString(parameterReaders)+", 从字节数组读取到的参数="+ Arrays.toString(paramsContext.getCollectedParameters()));
        }
        return paramsContext.getCollectedParameters();
    }
}
