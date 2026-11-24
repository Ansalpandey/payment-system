package com.squareupdigital.paymentsystem.dto;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext
) {}
