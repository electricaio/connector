package io.electrica.connector.test;

import io.electrica.connector.spi.service.Logger;
import org.slf4j.Marker;

public class Slf4JLoggerDelegate implements Logger {

    private final org.slf4j.Logger delegate;
    private final Marker marker;

    Slf4JLoggerDelegate(org.slf4j.Logger delegate, Marker marker) {
        this.delegate = delegate;
        this.marker = marker;
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(String msg) {
        delegate.info(marker, msg);
    }

    @Override
    public void info(String format, Object arg) {
        delegate.info(marker, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        delegate.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        delegate.info(marker, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        delegate.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(String msg) {
        delegate.warn(marker, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        delegate.warn(marker, format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        delegate.warn(marker, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        delegate.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        delegate.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(String msg) {
        delegate.error(marker, msg);
    }

    @Override
    public void error(String format, Object arg) {
        delegate.error(marker, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        delegate.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        delegate.error(marker, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        delegate.error(marker, msg, t);
    }
}
