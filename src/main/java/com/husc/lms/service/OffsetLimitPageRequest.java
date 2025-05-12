package com.husc.lms.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetLimitPageRequest implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public OffsetLimitPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset index must not be less than zero!");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("Limit must not be less than one!");
        }
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        // Represents the "page number" if data were divided into chunks of 'limit'
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageRequest(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return (offset - limit >= 0) ? new OffsetLimitPageRequest(offset - limit, limit, sort)
                : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageRequest(0, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }

    @Override
    public Pageable withPage(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero!");
        }
        return new OffsetLimitPageRequest(pageNumber * limit, limit, sort);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof OffsetLimitPageRequest))
            return false;
        OffsetLimitPageRequest that = (OffsetLimitPageRequest) o;
        return offset == that.offset && limit == that.limit
                && java.util.Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(offset, limit, sort);
    }
}