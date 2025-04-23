package rom.brt.service;

import rom.brt.dto.Fragment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageHandler {
    public void handleMessage(String message) {
        List<String> fragmentList = Arrays.stream(message.split("\n")).toList();
        fragmentList.forEach(this::handleFragment);
    }

    public void handleFragment(String messageFragment) {
        Fragment fragment = Fragment.fromString(messageFragment);
        if (fragment == null) throw new IllegalArgumentException("Unknown message type");
    }
}
