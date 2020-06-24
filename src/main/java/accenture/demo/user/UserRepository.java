package accenture.demo.user;


import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {

  Optional<AppUser> findByEmail(String email);

  Optional<AppUser> findByCardId(String cardId);

  List<AppUser> findByUserRole(UserRole userRole);

}
