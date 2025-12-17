package com.oojog.oojogtest.dtos;

import java.util.List;

public record CategoryDto (
        String id,
        String name,
        String description,
        String parentId,
        List<CategoryDto> children
) {}

