package app.web.pavelk.server9.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseInfo {
    private Map<String, String> env;
    private Properties pr;
    private List<String> custom;
}
