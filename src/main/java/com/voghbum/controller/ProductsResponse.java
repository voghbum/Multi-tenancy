package com.voghbum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public final class ProductsResponse {

    @NotNull
    private List<@Valid ProductResponse> products;
}
