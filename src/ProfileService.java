import java.io.IOException;
import java.sql.SQLException;

public class ProfileService {
    private final ProfileDAO profileDAO;

    public  ProfileService(){ profileDAO = new ProfileDAO();}

    public boolean updateOrCreateProfile(Profile profile) throws SQLException {
        Profile existingProfile = profileDAO.getProfileByEmployeeId(profile.getEmployeeId());
        if (existingProfile == null) {
            return profileDAO.createProfile(profile);
        } else {
            return profileDAO.updateProfile(profile);
        }
    }
}
