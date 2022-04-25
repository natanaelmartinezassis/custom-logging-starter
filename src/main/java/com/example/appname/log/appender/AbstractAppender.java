package com.example.appname.log.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class AbstractAppender {

    public abstract void build(final LoggerContext context);

    public abstract AbstractAppender getAbstractAppender();

    protected void setAppenderToLogger(final String packageName,
                                       final String logLevel,
                                       final boolean additive,
                                       final Appender<ILoggingEvent> appender,
                                       final LoggerContext context) {
        final Logger logger = context.getLogger(packageName);
        logger.setAdditive(additive);
        logger.setLevel(Level.toLevel(logLevel));
        logger.addAppender(appender);
    }

    protected void addContextListener(final LoggerContext context) {
        final AppLoggerContextListener loggerContextListener = new AppLoggerContextListener();
        loggerContextListener.setContext(context);
        context.addListener(loggerContextListener);
    }

    class AppLoggerContextListener extends ContextAwareBase implements LoggerContextListener {

        @Override
        public boolean isResetResistant() {
            return true;
        }

        @Override
        public void onStart(final LoggerContext context) {
            getAbstractAppender().build(context);
        }

        @Override
        public void onReset(final LoggerContext context) {
            getAbstractAppender().build(context);
        }

        @Override
        public void onStop(final LoggerContext context) {
            // Nothing to do.
        }

        @Override
        public void onLevelChange(final Logger logger, final Level level) {
            logger.setLevel(level);
        }
    }

}
