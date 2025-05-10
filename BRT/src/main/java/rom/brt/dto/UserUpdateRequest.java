package rom.brt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank
    private String userName;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "MSISDN must be 11 digits")
    private String msisdn;
}
