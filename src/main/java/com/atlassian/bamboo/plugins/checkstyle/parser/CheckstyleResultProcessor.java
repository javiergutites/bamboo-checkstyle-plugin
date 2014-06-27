package com.atlassian.bamboo.plugins.checkstyle.parser;

import javax.annotation.Nonnull;

public interface CheckstyleResultProcessor {

    void onError(@Nonnull CheckstyleError error);
}
