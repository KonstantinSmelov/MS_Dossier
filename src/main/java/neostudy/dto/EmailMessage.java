package neostudy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neostudy.model.Theme;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailMessage {
    private String address;
    private Theme theme;
    private Long applicationId;
}
