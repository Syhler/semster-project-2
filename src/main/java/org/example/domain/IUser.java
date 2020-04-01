package org.example.domain;
import org.example.entity.*;
import java.util.List;

public interface IUser extends IDomainHandler {
   public Boolean createUser(UserEntity userEntity);

public Boolean removeUser(UserEntity userEntity);

public UserEntity getUserById(String id);

public List<UserEntity> getAllUser();

public Boolean updateUser(UserEntity userEntity);

public List<UserEntity> getUserByCompany(CompanyEntity companyEntity);

public List<UserEntity> getUserByRole(Role role);
}