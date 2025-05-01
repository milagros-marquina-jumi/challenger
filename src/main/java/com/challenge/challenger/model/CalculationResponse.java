package com.challenge.challenger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record CalculationResponse(
    double num1,
    double num2,
    double sum,
    double percentage,
    double result,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp
) {
}
