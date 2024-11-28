package com.leongao.magtime.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

public class TimberMultiTagTree extends Timber.DebugTree{
    @Nullable
    @Override
    protected String createStackElementTag(@NonNull StackTraceElement element) {
//        return String.format("(%s:%s)#%s",
//                element.getFileName(),
//                element.getLineNumber(),
//                element.getMethodName());
        // TODO: 格式与预期不符
        return String.format("[%s#%s:%s]",
                super.createStackElementTag(element),
                element.getMethodName(),
                element.getLineNumber());
    }
}
