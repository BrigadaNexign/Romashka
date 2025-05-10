package rom.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rom.crm.dto.response.UserResponse;
import rom.crm.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @GetMapping
//    public ResponseEntity<List<UserResponse>> getAllUsers(
//            @RequestParam(required = false, defaultValue = "name") String sortBy) {
//        return ResponseEntity.ok(userService.getAllUsers(sortBy));
//    }
//
//    @PutMapping
//    public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest request) {
//        return ResponseEntity.ok(userService.saveUser(request));
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
//        return ResponseEntity.ok(userService.getUser(userId));
//    }
}