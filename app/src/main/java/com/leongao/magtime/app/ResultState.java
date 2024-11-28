package com.leongao.magtime.app;

public class ResultState<T> {
    private final T data;
    private final String error;

    public ResultState(T data, String error) {
        this.data = data;
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
