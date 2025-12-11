package vn.dungjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dungjava.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User getByEmail(String email);
}
