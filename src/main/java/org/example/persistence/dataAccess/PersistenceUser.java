package org.example.persistence.dataAccess;

import org.example.persistence.entities.CompanyEntity;
import org.example.persistence.entities.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceUser extends BasePersistence {


    private Connection connection;

    public PersistenceUser()
    {
        connection = initializeDatabase();
    }

    public long createUser(UserEntity userEntity , String encryptedPassword, String passwordSalt) {
        java.util.Date utilDate = userEntity.getCreatedAt();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "Insert INTO \"user\" (title,firstname, middlename, lastname, createdby, createdat, email, password, passwordsalt, role, company, timestamp_for_deletion)" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?) returning id;");
            preparedStatement.setString(1,userEntity.getTitle());
            preparedStatement.setString(2,userEntity.getFirstName());
            preparedStatement.setString(3,userEntity.getMiddleName());
            preparedStatement.setString(4,userEntity.getLastName());
            preparedStatement.setLong(5,userEntity.getCreatedBy().getId());
            preparedStatement.setDate(6,sqlDate);
            preparedStatement.setString(7,userEntity.getEmail());
            preparedStatement.setString(8,encryptedPassword);
            preparedStatement.setString(9,passwordSalt);
            preparedStatement.setInt(10,userEntity.getRole());
            preparedStatement.setLong(11,userEntity.getCompanyEntity().getId());
            preparedStatement.setTimestamp(12,null);
            var resultSet = preparedStatement.executeQuery();
            //checks if the resultSet contains any rows
            if (!resultSet.next())
            {
                return 0;
            }


                return resultSet.getLong("id");

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * Search the database for a user based on the given ID
     * @param id of the user you want to find
     * @return a UserEntity object if a user has been found
     */
    public UserEntity getUserById(long id) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "select \"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role,  company.id, company.name,\"user\".company  " +
                    "from \"user\", company where \"user\".id = ?  and company.id = \"user\".company");
            preparedStatement.setLong(1, id);



            var resultSet = preparedStatement.executeQuery();
            //checks if the resultSet contains any rows
            if (!resultSet.next())
            {
                return null;
            }


            return createUserEntityFromResultSet(resultSet);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updating user with password change
     * @param userEntity
     * @param encryptedPassword
     * @param passwordSalt
     * @return
     */
    public boolean updateUser(UserEntity userEntity, String encryptedPassword, String passwordSalt) {
        java.util.Date utilDate = userEntity.getCreatedAt();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "UPDATE \"user\" SET title = ?, firstname = ?,middlename = ?," +
                    "lastname = ?,createdat = ?,email = ?,password = ?,passwordsalt = ?,role = ?,company = ?" +
                    "WHERE \"user\".id = ?;");
            preparedStatement.setString(1,userEntity.getTitle());
            preparedStatement.setString(2,userEntity.getFirstName());
            preparedStatement.setString(3,userEntity.getMiddleName());
            preparedStatement.setString(4,userEntity.getLastName());
            preparedStatement.setDate(5,sqlDate);
            preparedStatement.setString(6,userEntity.getEmail());
            preparedStatement.setString(7,encryptedPassword);
            preparedStatement.setString(8,passwordSalt);
            preparedStatement.setInt(9,userEntity.getRole());
            preparedStatement.setLong(10,userEntity.getCompanyEntity().getId());
            preparedStatement.setLong(11,userEntity.getId());

            preparedStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updating user without password change
     * @param userEntity
     * @return
     */
    public boolean updateUser(UserEntity userEntity) {
        java.util.Date utilDate = userEntity.getCreatedAt();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {

            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "UPDATE \"user\" SET title = ?, firstname = ?,middlename = ?," +
                    "lastname = ?,createdat = ?,email = ?,role = ?,company = ?" +
                    "WHERE \"user\".id = ?;");
            preparedStatement.setString(1,userEntity.getTitle());
            preparedStatement.setString(2,userEntity.getFirstName());
            preparedStatement.setString(3,userEntity.getMiddleName());
            preparedStatement.setString(4,userEntity.getLastName());
            preparedStatement.setDate(5,sqlDate);
            preparedStatement.setString(6,userEntity.getEmail());
            preparedStatement.setInt(7,userEntity.getRole());
            preparedStatement.setLong(8,userEntity.getCompanyEntity().getId());
            preparedStatement.setLong(9,userEntity.getId());

            preparedStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<UserEntity> getAllDeletedUsers(){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT \"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role," +
                    "company.id, company.name, \"user\".company, company, \"user\".timestamp_for_deletion FROM \"user\" " +
                    "inner join company on company.id = \"user\".company " +
                    "where \"user\".timestamp_for_deletion is not null");
            var resultSet = preparedStatement.executeQuery();
            List<UserEntity> list = new ArrayList<>();
            while (resultSet.next()){
               list.add(createUserEntityFromResultSet(resultSet));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean unDeleteUser(UserEntity userEntity){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update \"user\" set timestamp_for_deletion = ? where id = ?");
            preparedStatement.setTimestamp(1,null);
            preparedStatement.setLong(2,userEntity.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    public boolean deleteUser(UserEntity userEntity) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "update \"user\" set timestamp_for_deletion = ? WHERE \"user\".id = ?;");
            preparedStatement.setTimestamp(1,new Timestamp(System.currentTimeMillis()));
            preparedStatement.setLong(2,userEntity.getId());
            preparedStatement.execute();


        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<UserEntity> getUserByRole(int roleId) {
        List<UserEntity> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "select \"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role,  " +
                    "company.id, company.name, \"user\".company, \"user\".timestamp_for_deletion from \"user\" " +
                    "left join company on company.id = \"user\".company where \"user\".role = ? ORDER BY \"user\".id ASC");
            preparedStatement.setInt(1, roleId);

            var resultSet = preparedStatement.executeQuery();

            //checks if the resultSet contains any rows
           while (resultSet.next()){
               if(resultSet.getTimestamp("timestamp_for_deletion") == null) {
                   users.add(createUserEntityFromResultSet(resultSet));
               }
               }

            return users;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public List<UserEntity> getUserByCompany(CompanyEntity companyEntity, int roleId) {
        List<UserEntity>users= new ArrayList<>();
        try{
            PreparedStatement prepStatement = connection.prepareStatement(
                    "select\"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role,"+
                            "company.id, company.name, \"user\".company, \"user\".timestamp_for_deletion from \"user\" "+
                            " inner join company on company.id = \"user\".company where \"user\".company = ? and \"user\".role = ? " +
                            "and \"user\".timestamp_for_deletion is null ORDER BY \"user\".id ASC");
            prepStatement.setLong(1,companyEntity.getId());
            prepStatement.setInt(2, roleId);
            var ResultSet = prepStatement.executeQuery();

            while(ResultSet.next()) {
                    users.add(createUserEntityFromResultSet(ResultSet));
                }

                return users;

        }catch(SQLException e){
            e.printStackTrace();

        }

        return null;
    }

    public List<UserEntity> getAllUsers() {
        List<UserEntity> users = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "select \"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role,  company.id, company.name, \"user\".company, \"user\".timestamp_for_deletion " +
                    "from \"user\", company ORDER BY \"user\".id ASC");

            var resultSet = preparedStatement.executeQuery();

            //checks if the resultSet contains any rows
            while (resultSet.next()){
                if(resultSet.getTimestamp("user.timestamp_for_deletion") == null) {
                    users.add(createUserEntityFromResultSet(resultSet));
                }
            }

            return users;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * searching in the database for a user based on the given username and hashedPassword
     * @return a UserEntity object if a user has been found
     */
    public UserEntity getUserByLoginInformation(String username, String password)
    {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("" +
                    "select \"user\".id, title, firstName, middleName, lastName, createdBy, createdAt, email, role,  company.id, company.name, \"user\".company " +
                    "from \"user\", company where email = ? and password = ?  and company.id = \"user\".company and \"user\".timestamp_for_deletion is null ");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            var resultSet = preparedStatement.executeQuery();

            //checks if the resultSet contains any rows
            if (!resultSet.next())
            {
                return null;
            }

            return createUserEntityFromResultSet(resultSet);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * searching in the database for a password salt based on the given username
     * @return password salt if found
     */
    public String getPasswordSaltFromUsername(String username) {

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT passwordSalt from \"user\" where email = ?");
            preparedStatement.setString(1, username);

            var resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
            {
                return null;
            }

            return resultSet.getString("passwordSalt");

        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * Generates a UserEntity from the given resultSet
     * @param resultSet the result set from a executed preparedStatement
     * @return A Object of UserEntity
     * @throws SQLException if the given resultSet doesn't have the required column.
     */
    private UserEntity createUserEntityFromResultSet(ResultSet resultSet) throws SQLException {
        var user = new UserEntity(
                        resultSet.getLong(1),  // 1 equal user id
                        resultSet.getString("title"),
                        resultSet.getString("firstName"),
                        resultSet.getString("middleName"),
                        resultSet.getString("lastName"),
                        resultSet.getDate("createdAt"),
                        resultSet.getString("email"),
                        resultSet.getInt("role"));

        if (resultSet.getString("name") != null)
        {
            var company = new CompanyEntity(
                    resultSet.getLong(12), //12 equal company id
                    resultSet.getString("name"));
            user.setCompanyEntity(company);
            //user.setCompanyName(company.getName());
        }

        //Makes a recursion call. It will loop through until an user isn't createdBy is null
        if (resultSet.getString("createdBy") != null)
        {
            user.setCreatedBy(getUserById(resultSet.getLong("createdBy")));
            //user.setCreatedByName(user.getCreatedBy().getFirstName());
        }

        return user;

    }
}
