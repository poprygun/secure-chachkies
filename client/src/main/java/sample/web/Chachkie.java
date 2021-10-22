package sample.web;

import lombok.Data;

import java.util.UUID;

@Data
public class Chachkie {
    private UUID id;
    private Double latitude;
    private Double longitude;
}
