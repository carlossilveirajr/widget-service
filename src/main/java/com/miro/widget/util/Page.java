package com.miro.widget.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

public class Page {

    private final int page;

    private final int size;

    private Page(final int page, final int size) {
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

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final Page page1 = (Page) o;
        return page == page1.page &&
                size == page1.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }

    public static Page from(final int page, final int size) {
        return new Page(page, size);
    }

}
