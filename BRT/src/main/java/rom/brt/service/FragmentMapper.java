package rom.brt.service;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import rom.brt.dto.Fragment;
import rom.brt.exceptions.CsvParsingException;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

@Component
public class FragmentMapper {
    public List<Fragment> parseCsv(String csvContent) throws CsvParsingException {
        try (Reader reader = new StringReader(csvContent)) {
            return new CsvToBeanBuilder<Fragment>(reader)
                    .withType(Fragment.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new CsvParsingException(e);
        }
    }
}
