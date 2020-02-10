package kr.co.mentalK94.restaurantReservation.interfaces;

import kr.co.mentalK94.restaurantReservation.application.RestaurantService;
import kr.co.mentalK94.restaurantReservation.domain.MenuItem;
import kr.co.mentalK94.restaurantReservation.domain.Restaurant;
import kr.co.mentalK94.restaurantReservation.domain.RestaurantNotFoundException;
import kr.co.mentalK94.restaurantReservation.domain.Review;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    public void list() throws Exception {

        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(Restaurant.builder().id(1103L).name("Lotteria").address("Anyang").build());

        given(restaurantService.getRestaurants()).willReturn(restaurants);

        mvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":1103")))
                .andExpect(content().string(containsString("\"name\":\"Lotteria\"")));
    }

    @Test
    public void detailWithExistedTest() throws Exception{

        Restaurant restaurant = Restaurant.builder().id(1001L).name("pizza school").address("Anyang").build();
        restaurant.setMenuItems(Arrays.asList(MenuItem.builder().name("potato pizza").build()));
        Review review = Review.builder().writer("hansol").score(4.5).description("great tasty!").build();

        restaurant.setReviews(Arrays.asList(review));

        given(restaurantService.getRestaurantById(1001L)).willReturn(restaurant);

        mvc.perform(get("/restaurants/1001"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"id\":1001")))
                .andExpect(content().string(containsString("\"name\":\"pizza school\"")))
                .andExpect(content().string(containsString("hansol")))
                .andExpect(content().string(containsString("great tasty!")))
                .andExpect(content().string(containsString("potato pizza")));

    }

    @Test
    public void detailWithNotExistedTest() throws Exception{

        given(restaurantService.getRestaurantById(404L)).willThrow(new RestaurantNotFoundException(404L));

        mvc.perform(get("/restaurant/404"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("{}"));
    }

    @Test
    public void createWithValidData() throws Exception {
        given(restaurantService.addRestaurant(any())).will(invocation -> {
            Restaurant restaurant = invocation.getArgument(0);
            return Restaurant.builder()
                        .id(1L)
                        .name(restaurant.getName())
                        .address(restaurant.getAddress())
                        .build();
        });

        mvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bukyung\", \"address\":\"Gunpo\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/restaurants/1"))
                .andExpect(content().string("{}"));

        verify(restaurantService).addRestaurant(any());
    }

    @Test
    public void createWithInvalidData() throws Exception {
        mvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\", \"address\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateWithValidData() throws Exception {

        mvc.perform(patch("/restaurants/1004")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"JOKER HOUSE\", \"address\":\"Seoul\"}"))
                .andExpect(status().isOk());

        verify(restaurantService).updateRestaurant(1004L, "JOKER HOUSE", "Seoul");
    }

    @Test
    public void updateWithInvalidData() throws Exception {

        mvc.perform(patch("/restaurants/1004")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\", \"address\":\"\"}"))
                .andExpect(status().isBadRequest());

    }
}