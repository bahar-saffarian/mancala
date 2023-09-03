package com.bol.mancala.swagger;

import com.bol.mancala.dto.BoardResponse;
import com.bol.mancala.exceptions.ApiError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BoardResponse.class))),
        @ApiResponse(responseCode = "400", description = "business error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "500", description = "unknown error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
})
public @interface ApiGeneralResponses {
}
