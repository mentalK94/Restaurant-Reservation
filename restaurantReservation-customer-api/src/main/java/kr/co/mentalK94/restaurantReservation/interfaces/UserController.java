package kr.co.mentalK94.restaurantReservation.interfaces;

import io.jsonwebtoken.Claims;
import kr.co.mentalK94.restaurantReservation.application.UserService;
import kr.co.mentalK94.restaurantReservation.domain.Review;
import kr.co.mentalK94.restaurantReservation.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody User resource) throws URISyntaxException {

        User user = userService.addUser(resource.getEmail(), resource.getPassword(),
                            resource.getName(), resource.getPhone(),
                            resource.getAddress());

        String url = "/users/"+user.getId();
        return ResponseEntity.created(new URI(url)).body("{}");
    }

}
