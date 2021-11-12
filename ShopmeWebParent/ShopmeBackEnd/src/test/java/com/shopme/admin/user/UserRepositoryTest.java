package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserNewUserWithOneRole() {
	Role roleAdmin = entityManager.find(Role.class, 1);
	User userLongBD = new User("duclong2kzz@gmail.com", "123123", "Long", "Duc");
	userLongBD.setEnable(true);
	userLongBD.addRole(roleAdmin);

	User savedUser = repo.save(userLongBD);
	assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserNewUserWithTwoRole() {
	User userTuan = new User("tuannt3@fsoft.com.vn", "123456", "Tran", "Tuan");
	Role roleEditor = new Role(3);
	Role roleAssistant = new Role(5);
	userTuan.addRole(roleEditor);
	userTuan.addRole(roleAssistant);

	User savedUser = repo.save(userTuan);

	assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
	Iterable<User> listUsers = repo.findAll();
	listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
	User find = repo.findById(1).get();
	System.out.println(find);
	assertThat(find).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
	User user = repo.findById(1).get();
	user.setEnable(true);
	user.setEmail("duclong2kzz@gmail.com");
	repo.save(user);
    }

    @Test
    public void testUpdateUserRoles() {
	User user = repo.findById(2).get();
	user.getRoles().remove(new Role(3));
	user.addRole(new Role(2));
	repo.save(user);
    }

    @Test
    public void testDeleteUser() {
	Integer userId = 2;
	repo.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail() {
	String email = "tuannt3@fsoft.com.vn";
	User userByEmail = repo.getUserByEmail(email);

	assertThat(userByEmail).isNotNull();
    }

    @Test
    public void testCountById() {
	Integer id = 1;
	Long countById = repo.countById(id);
	assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testUpdateStatus() {
	Integer id = 1;
	repo.updateEnableStatus(id, true);
    }

    @Test
    public void testListFirstPage() {
	int pageNumber = 0;
	int pageSize = 4;

	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	Page<User> page = repo.findAll(pageable);

	List<User> listUsers = page.getContent();
	listUsers.forEach(user -> System.out.println(user));

	assertThat(listUsers.size()).isEqualTo(pageSize);

    }

    @Test
    public void testGetById() {
	User user = repo.findById(1).get();
	System.out.println(user);
    }

    @Test
    public void testSearchUser() {
	String keyword = "bruce";

	int pageNumber = 0;
	int pageSize = 4;

	Pageable pageable = PageRequest.of(pageNumber, pageSize);
	Page<User> page = repo.findAll(keyword, pageable);

	List<User> listUsers = page.getContent();
	listUsers.forEach(user -> System.out.println(user));

	assertThat(listUsers.size()).isGreaterThan(0);
    }
}
