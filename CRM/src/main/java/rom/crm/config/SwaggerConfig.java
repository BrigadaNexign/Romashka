package rom.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI romashkaOpenAPI() throws IOException {
        // Load the YAML file from resources
        ClassPathResource resource = new ClassPathResource("static/romashka-swagger-ui.yml");

        if (resource.exists()) {
            // Read the file content
            String yamlContent = FileCopyUtils.copyToString(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            // Parse the YAML content using Swagger's parser
            ParseOptions options = new ParseOptions();
            options.setResolve(true);

            return new OpenAPIV3Parser().readContents(yamlContent, null, options).getOpenAPI();
        } else {
            // Fallback configuration
            return new OpenAPI()
                    .info(new io.swagger.v3.oas.models.info.Info()
                            .title("Romashka API")
                            .description("API documentation for Romashka system")
                            .version("v1.0")
                            );
        }
    }
}