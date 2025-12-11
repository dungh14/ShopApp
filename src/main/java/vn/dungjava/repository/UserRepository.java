package vn.dungjava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.dungjava.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User getByEmail(String email);

    @Query(value = "select u from User u where u.status = 'ACTIVE' " +
            "and (lower(u.firstName) like :keyword " +
            "or lower(u.lastName) like :keyword " +
            "or lower(u.phone) like :keyword " +
            "or lower(u.email) like :keyword)")
    Page<User> searchByKeywords(String keyword, Pageable pageable);
}
