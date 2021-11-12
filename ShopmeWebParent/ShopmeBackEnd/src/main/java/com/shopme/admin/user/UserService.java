package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
	return (List<User>) userRepo.findAll();
    }

    public User getByEmail(String email) {
	User userByEmail = userRepo.getUserByEmail(email);
	return userByEmail;
    }

    public User getById(Integer id) {
	return userRepo.findById(id).get();
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
	Sort sort = Sort.by(sortField);
	sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

	Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
	if (keyword != null) {
	    return userRepo.findAll(keyword, pageable);
	}
	return userRepo.findAll(pageable);
    }

    public List<Role> listRoles() {
	return (List<Role>) roleRepo.findAll();
    }

    public User save(User user) {
	boolean isUpdatingUser = (user.getId() != null);

	if (isUpdatingUser) {
	    User existingUser = userRepo.findById(user.getId()).get();

	    if (user.getPassword().isEmpty()) {
		user.setPassword(existingUser.getPassword());
	    } else {
		encodePassword(user);
	    }
	} else {
	    encodePassword(user);
	}
	return userRepo.save(user);
    }

    public User updateAccount(User user) {
	User userInDB = userRepo.findById(user.getId()).get();
	if (!user.getPassword().isEmpty()) {
	    userInDB.setPassword(user.getPassword());
	    encodePassword(userInDB);
	}
	if (user.getPhotos() != null) {
	    userInDB.setPhotos(user.getPhotos());
	}
	userInDB.setFirstName(user.getFirstName());
	userInDB.setLastName(user.getLastName());
	return userRepo.save(userInDB);
    }

    private void encodePassword(User user) {
	String encodedPassword = passwordEncoder.encode(user.getPassword());
	user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
	User userByEmail = userRepo.getUserByEmail(email);
	if (userByEmail == null)
	    return true;

	boolean isCreatingNew = (id == null);

	if (isCreatingNew) {
	    if (userByEmail != null)
		return false;
	} else {
	    if (userByEmail.getId() != id) {
		return false;
	    }
	}

	return true;
    }

    public User get(Integer id) throws UserNotFoundException {
	try {
	    return userRepo.findById(id).get();
	} catch (NoSuchElementException e) {
	    throw new UserNotFoundException("Cound not find any user with ID " + id);
	}
    }

    public void delete(Integer id) throws UserNotFoundException {
	Long countById = userRepo.countById(id);
	if (countById == null || countById == 0) {
	    throw new UserNotFoundException("Cound not find any user with ID " + id);
	}
	userRepo.deleteById(id);
    }

    public void updateUserStatus(Integer id, boolean enabled) {
	userRepo.updateEnableStatus(id, enabled);
    }

}
