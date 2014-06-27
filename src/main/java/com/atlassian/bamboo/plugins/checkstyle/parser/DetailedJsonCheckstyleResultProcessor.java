package com.atlassian.bamboo.plugins.checkstyle.parser;

import javax.annotation.Nonnull;

/**
 * A processor that builts a detailed JSON entity containing all checkstyle notifications.
 */
public class DetailedJsonCheckstyleResultProcessor implements CheckstyleResultProcessor {

    @Override
    public void onError(@Nonnull CheckstyleError error) {

    }
}
