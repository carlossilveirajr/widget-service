package com.miro.widget.util;

import static com.google.common.base.Preconditions.checkArgument;

public class Page {

    private final int page;

    private final int size;

    public Page(final int page, final int size) {
        checkArgument(page >= 0, "Page must be positive");
        checkArgument(size > 0, "Page size must be greater then one");

        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

}
