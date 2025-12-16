package vn.dungjava.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    User findByUsername(String username);

    @Query("""
        select distinct u from User u
        left join fetch u.roles ur
        left join fetch ur.role r
        where u.username = :username
    """)
    User findByUsernameFetchRoles(@Param("username") String username);
}
