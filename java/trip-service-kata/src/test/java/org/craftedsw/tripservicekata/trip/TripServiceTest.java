package org.craftedsw.tripservicekata.trip;

        import org.craftedsw.tripservicekata.exception.UserNotLoggedInException;
        import org.craftedsw.tripservicekata.user.User;
        import org.junit.Assert;
        import org.junit.Before;
        import org.junit.Test;

        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Queue;

public class TripServiceTest {

    private static final User NOT_LOGGED_IN_USER = null;
    public static final User LOGGED_IN_USER = new User();
    private static final User ANOTHER_USER = new User();
    private static final Trip LONDON = new Trip();
    private static final Trip TOULOUSE = new Trip();
    private static final Trip PARIS = new Trip();
    private TripService service;
    private User loggedInUser = null;

    @Before
    public void setUp() {
        service = new TestableTripService();
    }

    @Test(expected = UserNotLoggedInException.class)
    public void
    should_throws_an_exception_when_user_is_not_logged_in() {
        loggedInUser = NOT_LOGGED_IN_USER;
        TripService service = new TestableTripService();

        service.getTripsByUser(new User());
    }

    @Test()
    public void
    should_return_an_empty_list_when_users_are_not_friends() {
        loggedInUser = LOGGED_IN_USER;
        User unknown =  UserBuilder.aUser().withFriends(ANOTHER_USER)
                .withTripsTo(LONDON, TOULOUSE, PARIS)
                .build();

        Assert.assertEquals(new ArrayList<Trip>(), service.getTripsByUser(unknown));
    }

    @Test public void
    should_return_list_of_trips_when_users_are_friends() {
        loggedInUser = LOGGED_IN_USER;
        User friend =  UserBuilder.aUser().withFriends(LOGGED_IN_USER, ANOTHER_USER)
                                  .withTripsTo(LONDON, TOULOUSE, PARIS)
                                  .build();

        List<Trip> tripList = service.getTripsByUser(friend);

        Assert.assertEquals(3, tripList.size());
    }

    class TestableTripService extends TripService {
        @Override
        protected User loggedInUser() {
            return loggedInUser;
        }

        @Override
        protected List<Trip> findTripsByUser(User user) {
            return user.trips();
        }

    }

    private static class UserBuilder {
        private List<User> friends = new ArrayList<User>();
        private List<Trip> trips = new ArrayList<Trip>();

        public static UserBuilder aUser() {
            return new UserBuilder();
        }

        public UserBuilder withFriends(User...friends) {
            for(User friend: friends) {
                this.friends.add(friend);
            }

            return this;
        }

        public UserBuilder withTripsTo(Trip...trips) {
            for(Trip trip:trips) {
                this.trips.add(trip);
            }

            return this;
        }

        public User build() {
            User user = new User();
            for(Trip trip: trips)
                user.addTrip(trip);

            for(User friend: friends)
                user.addFriend(friend);

            return user;
        }
    }
}

